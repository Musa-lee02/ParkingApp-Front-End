package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.Credential

import java.time.LocalDate

data class SaveUser(
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val credential: Credential,
)