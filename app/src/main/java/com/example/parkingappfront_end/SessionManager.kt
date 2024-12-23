package com.example.parkingappfront_end

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.parkingappfront_end.model.AccessToken
import com.example.parkingappfront_end.model.RefreshToken
import com.example.parkingappfront_end.model.User
import com.example.parkingappfront_end.model.UserId

import com.auth0.android.jwt.JWT
import com.example.parkingappfront_end.model.Position
import com.example.parkingappfront_end.network.RetrofitClient
import com.example.parkingappfront_end.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random


object SessionManager {

    private const val PREFS_NAME = "user_session"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val REFRESH_TOKEN_KEY  = "refresh_token"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    var user: User? = null
        private set

    var position: Position? = Position.default()
        private set

    var authToken: String? = null
        private set

    var refreshToken: String? = null
        private set

    private var authRepository : AuthRepository? = null

    private val _observableUser = MutableStateFlow<User?>(null)
    val observableUser = _observableUser

    fun init(context: Context) {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        setAuthRepository(AuthRepository(RetrofitClient.authApiService))

        loadSession()
    }

    private fun getPrefs(): SharedPreferences {
        return prefs ?: throw IllegalStateException("SessionManager not initialized. Call init() first.")
    }

    private  fun loadSession() {
        runBlocking {
            launch {
                authToken = getPrefs().getString(KEY_AUTH_TOKEN, null)
                refreshToken = getPrefs().getString(REFRESH_TOKEN_KEY, null)
                println("authToken: $authToken")
                println("refreshToken: $refreshToken")

                val tokenMustValidated = authToken
                val _refreshToken = refreshToken

                if (tokenMustValidated != null && _refreshToken != null) {
                    val validatedToken = authRepository?.validateToken(AccessToken(tokenMustValidated))
                    println("validatedToken: $validatedToken")
                    println("validatedTokenBody ${validatedToken?.body()}")
                    println("validatedTokenCode ${validatedToken?.code()}")
                    if (validatedToken != null && validatedToken.isSuccessful) {
                        authToken = tokenMustValidated
                        user = decodeJwtToken(tokenMustValidated)
                        _observableUser.value = user
                        println("user: ${user?.firstName}, ${user?.lastName}")
                        return@launch
                    } else if (validatedToken != null && validatedToken.code() == 401) {
                        val tokenResponse =
                            user?.let { authRepository?.refreshToken(it.id,RefreshToken(_refreshToken)) }
                        if (tokenResponse != null) {
                            println( "tokenResponseBody: ${tokenResponse.body()}")
                            println("tokenResponseCode: ${tokenResponse.code()}")
                        }

                        if (tokenResponse != null) {

                            authToken = tokenResponse.body()?.accessToken
                            refreshToken = tokenResponse.body()?.refreshToken
                            authToken?.let {

                                user = decodeJwtToken(it)
                                _observableUser.value = user
                                println("Updated observableUser: ${_observableUser.value}")
                            }
                            getPrefs().edit().putString(KEY_AUTH_TOKEN, authToken).apply()
                            getPrefs().edit().putString(REFRESH_TOKEN_KEY, refreshToken).apply()
                            return@launch
                        }
                    }
                }
                clearSession()
            }
        }
    }

    fun saveAuthToken(token: String){
        authToken = token
        getPrefs().edit().putString(KEY_AUTH_TOKEN, token).apply()
        user = decodeJwtToken(token)
        _observableUser.value = user
        Log.d(TAG, "user: ${user?.firstName}, ${user?.lastName}")
    }

    fun saveRefreshToken(token: String){
        refreshToken = token
        getPrefs().edit().putString(REFRESH_TOKEN_KEY, token).apply()
    }

    fun clearSession(){
        getPrefs().edit().clear().apply()
        authToken = null
        refreshToken = null
        user = null
    }

    private fun decodeJwtToken(token: String): User? {
        Log.d(TAG, "decodeJwtToken: $token")
        val jwt = JWT(token)
        val userIdS = jwt.getClaim("userId").asString()
        val email = jwt.getClaim("sub").asString()
        val firstName = jwt.getClaim("firstName").asString()
        val lastName = jwt.getClaim("lastName").asString()
        val birthdate = jwt.getClaim("birthdate").asString()
        val role = jwt.getClaim("role").asString()
        Log.d(TAG, "decodeJwtToken Role: $role")

        return if (userIdS != null && email != null && firstName != null && lastName != null && birthdate != null  && role != null) {
            val userId = UUID.fromString(userIdS)
            User(userId, firstName, lastName, LocalDate.parse(birthdate), role)
        } else {
            null
        }

    }

    fun setAuthRepository(authRepository: AuthRepository){
        this.authRepository = authRepository
    }

    fun getUser(): UserId {
        println("Sto recuperando l'utente")
        val userId = user?.id ?: throw IllegalStateException("User not logged in")
        println("UserId recuperato: $userId")
        return UserId(userId)
    }

}