package com.example.parkingappfront_end.model

data class ParkingSpot (
    val id: Long? = null,
    val number: String,
    val parkingSpaceId: Long,
    val reservations: List<Reservation>? = ArrayList(),
    val basePrice: Double,
)