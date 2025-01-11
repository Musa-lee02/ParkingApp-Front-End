package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.domain.PaymentMethodType
import java.time.LocalDateTime

data class Reservation(
    val id: Long?,
    val user: User,
    val price: Double,
    val paymentMethod: PaymentMethodType,
    val parkingSpotId: Long,
    val licensePlate: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)