package com.example.parkingappfront_end.model

import java.time.LocalDateTime

data class Reservation(
    val id: Long?,
    val user: User,
    val price: Double,
    val parkingSpotId: Long,
    val licensePlate: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)