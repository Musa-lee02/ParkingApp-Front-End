package com.example.parkingappfront_end.model

data class SavePaymentMethod(

        val user : UserId,
        val cardHolderName : String,
        val paymentMethodType : PaymentMethodType?,
        val provider : CardProvider?,
        val cardNumber: String,
        val expirationDate: String
)
