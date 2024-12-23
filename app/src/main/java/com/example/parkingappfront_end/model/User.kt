package com.example.parkingappfront_end.model

import java.time.LocalDate
import java.util.UUID

data class User(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val role: String? = null,
)