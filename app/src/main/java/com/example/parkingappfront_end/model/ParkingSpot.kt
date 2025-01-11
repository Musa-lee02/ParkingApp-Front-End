package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.domain.SpotType

data class ParkingSpot (
    val id: Long? = null,
    val number: String,
    val type: SpotType,
    val parkingSpaceId: Long,
    val reservations: List<Reservation>? = ArrayList(),
    val basePrice: Double,
)