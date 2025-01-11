package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.domain.PaymentMethodType
import com.example.parkingappfront_end.model.domain.SpotType
import java.time.LocalDateTime

data class ReservationWithDetails(
    val id: Long?,
    val user: User,
    val price: Double,
    val paymentMethod: PaymentMethodType,
    val parkingSpotId: Long,
    val licensePlate: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,

    val spaceName: String? = null,
    val spotNumber: String? = null,
    val spotType: SpotType? = null
)