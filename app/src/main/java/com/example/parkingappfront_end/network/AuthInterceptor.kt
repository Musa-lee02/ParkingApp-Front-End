package com.example.parkingappfront_end.network


import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.RefreshToken
import com.example.parkingappfront_end.model.RequiresAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation


class AuthInterceptor(
    private val sessionManager: SessionManager,
) : Interceptor {
    private var authApiService: AuthApiService? = null

    fun setAuthApiService(authApiService: AuthApiService) {
        this.authApiService = authApiService
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        setAuthApiService(RetrofitClient.authApiService)
        val authApiService = authApiService ?: throw IllegalStateException("AuthApiService is null")
        val originalRequest = chain.request()
        //println("Intercepting request: ${originalRequest.url}")

        val requiresAuth = originalRequest.tag(Invocation::class.java)?.method()?.getAnnotation(
            RequiresAuth::class.java) != null
        println("Requires auth: $requiresAuth")

        if (requiresAuth) {

            val accessToken = sessionManager.authToken

            val requestBuilder = originalRequest.newBuilder()
            accessToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            val requestWithAuth = requestBuilder.build()
            println("Request with auth: $requestWithAuth")
            val response = chain.proceed(requestWithAuth)
            println("Response code: ${response.code}")

            if (response.code == 401) {


                    val refreshToken = sessionManager.refreshToken
                    if (refreshToken != null) {
                        val newTokensResponse = runBlocking { withContext(Dispatchers.IO) {authApiService.refreshToken(
                            RefreshToken(refreshToken)
                        ) } }
                        if (newTokensResponse.isSuccessful) {
                            val newAccessToken = newTokensResponse.body()?.accessToken
                            val newRefreshToken = newTokensResponse.body()?.refreshToken

                            if (newAccessToken != null && newRefreshToken != null) {
                                sessionManager.saveAuthToken(newAccessToken)
                                sessionManager.saveRefreshToken(newRefreshToken)

                                val newRequest = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer $newAccessToken")
                                    .build()

                                response.close()

                                return chain.proceed(newRequest)
                            } else {
                                // Se il refresh token non funziona, logout o richiedi il login
                                sessionManager.clearSession()
                                // Puoi anche lanciare un'eccezione o gestire il logout in altro modo
                                //FIXME logout + redirect to login
                            }
                        }
                    }
                }


        return response
    }
        else {
            println("Request without auth" + originalRequest.url)
            return chain.proceed(originalRequest)
        }
    }
}
