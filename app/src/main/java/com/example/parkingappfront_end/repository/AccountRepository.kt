package com.example.parkingappfront_end.repository


import com.example.parkingappfront_end.model.Email
import com.example.parkingappfront_end.model.PasswordUser
import com.example.parkingappfront_end.network.UserApiService
import java.util.UUID

class AccountRepository(private val apiService : UserApiService) {

    suspend fun getLoggedInUser(userId : UUID) = apiService.getUser(userId)

    suspend fun editEmail(userId: UUID, email: String) = apiService.changeEmail(userId, Email(email))

    suspend fun getUserOrders(userId: UUID) = apiService.getUserOrders(userId)

    suspend fun changePassword(userId: UUID, password: PasswordUser) = apiService.changePassword(userId, password)

    suspend fun logout(userId: UUID) = apiService.logout(userId)

    suspend fun deleteAccount(userId: UUID) = apiService.deleteAccount(userId)
}