package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.PaymentMethod
import com.example.parkingappfront_end.model.RequiresAuth
import com.example.parkingappfront_end.model.SavePaymentMethod
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface CheckoutApiService {

    // Recupera l'indirizzo di spedizione dell'utente

    // Recupera la lista dei metodi di pagamento dell'utente
    @GET("paymentMethods/get/{userId}")
    @RequiresAuth
    suspend fun getPaymentMethods(@Path("userId") userId: UUID) : Response<List<PaymentMethod>?>

    @GET("paymentMethods/get/{userId}/{paymentMethodId}")
    @RequiresAuth
    suspend fun getPaymentMethod(
        @Path("userId") userId: UUID,
        @Path("paymentMethodId") paymentMethodId: Long
    ) : Response<PaymentMethod?>

    @DELETE("paymentMethods/delete/{paymentMethodId}/{userId}")
    @RequiresAuth
    suspend fun deletePaymentMethod(
        @Path("userId") userId: UUID,
        @Path("paymentMethodId") paymentMethodId: Long
    )

    // Aggiunge un nuovo metodo di pagamento
    @POST("paymentMethods/add")
    @RequiresAuth
    suspend fun addPaymentMethod(
        @Body paymentMethod: SavePaymentMethod
    ) : Response<PaymentMethod>

    /*
    @POST("orders/add")
    @RequiresAuth
    suspend fun addOrder(
        @Body checkoutRequest: CheckoutRequest
    ) : Response<SaveOrder>
    */
}