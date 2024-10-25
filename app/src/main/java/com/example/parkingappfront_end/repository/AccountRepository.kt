package com.example.parkingappfront_end.repository


import com.example.parkingappfront_end.model.Email
import com.example.parkingappfront_end.network.UserApiService
import java.util.UUID

class AccountRepository(private val apiService : UserApiService) {

    suspend fun getLoggedInUser(userId : UUID) = apiService.getUser(userId)

    suspend fun editEmail(userId: UUID, email: String) = apiService.changeEmail(userId, Email(email))

}