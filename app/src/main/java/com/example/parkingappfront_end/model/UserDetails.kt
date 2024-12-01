package com.example.parkingappfront_end.model
import java.util.UUID

data class UserDetails(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
)