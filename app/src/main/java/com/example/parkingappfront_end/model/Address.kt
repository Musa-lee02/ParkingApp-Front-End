package com.example.parkingappfront_end.model

data class Address(
    val id: Long,
    val street: String? = null,
    val city: String,
    val latitude: Double,
    val longitude: Double,

){
    companion object {
        fun default() = Address(1, "Via Roma", "Cosenza", 38.322, 40.0)
    }
}