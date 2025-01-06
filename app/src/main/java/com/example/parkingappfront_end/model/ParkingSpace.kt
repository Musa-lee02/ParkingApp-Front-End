package com.example.parkingappfront_end.model

import java.util.UUID

data class ParkingSpace (
    val id: Long ? = null,
    val userId: UserId,
    val parkingSpots: List<ParkingSpot> ? = null,
    val name: String,
    val address: Address,
) {
    companion object {
        fun default() = ParkingSpace( 0, UserId(UUID.randomUUID()), emptyList(), "", Address(
            0, "Via roma","Cosenza Default", 0.0, 0.0), )
    }
}