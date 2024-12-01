package com.example.parkingappfront_end.network


import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.CardProvider
import com.example.parkingappfront_end.model.CardProviderAdapter
import com.example.parkingappfront_end.model.PaymentMethodType
import com.example.parkingappfront_end.model.PaymentMethodTypeAdapter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.time.LocalDate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val Emulator1_URL = "https://10.0.2.2:8081/api/v1/"

    val client: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Configura il logging interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Logga il corpo della richiesta e della risposta
        }

        val allHostsValid = HostnameVerifier { _, _ -> true }
        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(allHostsValid)
            .addInterceptor(AuthInterceptor(SessionManager))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Emulator1_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).registerTypeAdapter(
                        PaymentMethodType::class.java,  PaymentMethodTypeAdapter() )
                    .registerTypeAdapter(CardProvider::class.java, CardProviderAdapter())
                    .create()
            ))
            .build()
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val adminApiService: AdminApiService by lazy {
        retrofit.create(AdminApiService::class.java)
    }



}

