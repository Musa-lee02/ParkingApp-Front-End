package com.example.parkingappfront_end.model

data class PaymentMethod (

    val id: Long,
    val user : UserId,
    val cardHolderName : String,
    val paymentMethodType : PaymentMethodType,
    val provider : CardProvider,
    val cardNumber: String,
    val expirationDate: String
)

