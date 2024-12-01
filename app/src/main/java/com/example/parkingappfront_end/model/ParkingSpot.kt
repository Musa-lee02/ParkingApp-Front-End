package com.example.parkingappfront_end.model

data class ParkingSpot (
    val id: Long,
    val number: String,
    val parkingSpaceId: ParkingSpace,
    val reservations: List<Reservation>
)