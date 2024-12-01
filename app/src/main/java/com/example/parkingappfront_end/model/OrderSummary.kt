package com.example.parkingappfront_end.model

data class OrderSummary(
    val orderId: Long,
    val email: String,
    val orderDate : String,
    val totalAmount : Double,
)
