package com.example.parkingappfront_end.model

import java.util.UUID

data class ParkingSpace (
    val id: Long,
    val userId: UserId,
    val parkingSpots: List<ParkingSpot>,
    val name: String,
    val address: String,
    val city: String,
) {
    companion object {
        fun default() = ParkingSpace( 0, UserId(UUID.randomUUID()), emptyList(), "", "", "")
    }
}