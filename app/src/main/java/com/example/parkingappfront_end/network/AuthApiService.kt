package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.AccessToken
import com.example.parkingappfront_end.model.SaveUser
import com.example.parkingappfront_end.model.Credential
import com.example.parkingappfront_end.model.RefreshToken
import com.example.parkingappfront_end.model.RefreshTokenResponse
import com.example.parkingappfront_end.model.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface AuthApiService {

    @POST("auth/{userId}/refreshToken")
    suspend fun refreshToken(@Path("userId") userId: UUID, @Body refreshToken: RefreshToken): Response<RefreshTokenResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body credentials: Credential): Response<Map<String, String>>

    @POST("auth/register")
    suspend fun registerUser(@Body user: SaveUser): Response<UserDetails>

    @POST("admin/register")
    suspend fun registerAdmin(@Body user: SaveUser): Response<UserDetails>

    @POST("auth/validate-token")
    suspend fun validateToken(@Body accessToken : AccessToken) : Response<AccessToken>

}