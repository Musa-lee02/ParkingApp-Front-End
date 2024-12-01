package com.example.parkingappfront_end.model

data class ParkingSpace (
    val id: Long,
    val owner: User,
    val parkingSpots: List<ParkingSpot>,
    val name: String,
    val address: String,
    val city: String,
)