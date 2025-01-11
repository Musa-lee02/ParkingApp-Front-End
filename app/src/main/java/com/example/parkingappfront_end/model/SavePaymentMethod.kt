package com.example.parkingappfront_end.model

import com.example.parkingappfront_end.model.domain.PaymentMethodType

data class SavePaymentMethod(

    val user : UserId,
    val cardHolderName : String,
    val paymentMethodType : PaymentMethodType?,
    val provider : CardProvider?,
    val cardNumber: String,
    val expirationDate: String
)
