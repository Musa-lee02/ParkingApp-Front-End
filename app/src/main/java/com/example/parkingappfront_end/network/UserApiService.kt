package com.example.parkingappfront_end.network


import com.example.parkingappfront_end.model.Email
import com.example.parkingappfront_end.model.RequiresAuth
import com.example.parkingappfront_end.model.User
import com.example.parkingappfront_end.model.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface UserApiService {

    @GET("users/{id}")
    @RequiresAuth
    suspend fun getUser(@Path("id") id: UUID) : UserDetails?

    @PUT("users/{id}/change-email")
    @RequiresAuth
    suspend fun changeEmail(@Path("id") id: UUID, @Body email: Email) : Response<User>

}