package com.example.parkingappfront_end.repository


import com.example.parkingappfront_end.model.PaymentMethod
import com.example.parkingappfront_end.model.SavePaymentMethod
import com.example.parkingappfront_end.network.CheckoutApiService
import retrofit2.Response
import java.util.UUID

class CheckoutRepository(private val checkoutApiService: CheckoutApiService) {


    // Recupera la lista dei metodi di pagamento
    suspend fun getPaymentMethods(userId: UUID) : Response<List<PaymentMethod>?> {
        return checkoutApiService.getPaymentMethods(userId)
    }



    suspend fun addPaymentMethod(paymentMethod: SavePaymentMethod): Response<PaymentMethod> {
        println("metodo di pagamento che sto salvando:$paymentMethod")
        return checkoutApiService.addPaymentMethod(paymentMethod)
    }

    suspend fun deletePaymentMethod(userId: UUID, paymentMethodId: Long) {
        println("metodo di pagamento che sto cancellando:$paymentMethodId")
        println("userId: $userId")
        return checkoutApiService.deletePaymentMethod(userId, paymentMethodId)
    }

    suspend fun getPaymentMethod(userId: UUID, paymentMethodId: Long) : Response<PaymentMethod?> {
        return checkoutApiService.getPaymentMethod(userId, paymentMethodId)
    }

}
