package com.example.parkingappfront_end.model

import java.util.Date

data class Reservation (
    val id: Long,
    val driver: User,
    val price: Double,
    val parkingSpotId: Long,
    val licensePlate: LicensePlate,
    val startDate: Date,
    val endDate: Date
)