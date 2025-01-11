package com.example.parkingappfront_end.model.domain

data class Position(
    val latitude: Double,
    val longitude: Double,
){
    companion object {
        fun default() = Position(
            41.9028, 12.4964
        )
    }
}