package com.example.parkingappfront_end.repository


import com.example.parkingappfront_end.model.AccessToken
import com.example.parkingappfront_end.model.Credential
import com.example.parkingappfront_end.model.RefreshToken
import com.example.parkingappfront_end.model.SaveUser
import com.example.parkingappfront_end.network.AuthApiService

class AuthRepository (private val apiService : AuthApiService) {

    suspend fun loginUser(credentials: Credential) = apiService.login(credentials)

    suspend fun registerUser(user: SaveUser) = apiService.register(user)

    suspend fun validateToken(accessToken : AccessToken) = apiService.validateToken(accessToken)

    suspend fun refreshToken(refreshToken: RefreshToken) = apiService.refreshToken(refreshToken)
}

