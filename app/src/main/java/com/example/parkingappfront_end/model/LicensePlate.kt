package com.example.parkingappfront_end.model

data class LicensePlate (
    val id: Long,
   val lpNumber: String,
    val user: User? = null
)