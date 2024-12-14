package com.example.parkingappfront_end.model

data class LicensePlate (
    private val id: Long,
    private val lpNumber: String,
    private val user: User?
)