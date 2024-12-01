package com.example.parkingappfront_end.model

import java.util.Date

data class Reservation (
    val id: Long,
    val parkingSpot: ParkingSpot,
    val customer: User,
    val licensePlate: LicensePlate,
    val startDate: Date,
    val endDate: Date
)