package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.RequiresAuth
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.ReservationWithDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface ReservationApiService {

    @GET("reservations/getAll")
    //@RequiresAuth
    suspend fun getAll(): Response<List<Reservation>>

    @GET("reservations/getByUser/{idUser}")
    @RequiresAuth
    suspend fun getById(@Path ("idUser") idUser: UUID): Response<List<Reservation>>

    @GET("reservations/getBySpot/{idSpot}")
    @RequiresAuth
    suspend fun getBySpot(@Path ("idSpot") idSpot: Long): Response<List<Reservation>>

    @GET("reservations/getWithDetails/{idUser}")
    @RequiresAuth
    suspend fun getWithDetails(@Path ("idUser") idUser: UUID): Response<List<ReservationWithDetails>>

    @PUT("reservations/add/{idUser}")
    @RequiresAuth
    suspend fun add( @Path ("idUser") idUser: UUID, @Body reservation: Reservation): Response<Reservation>

    @DELETE("reservations/delete/{idRes}/{idUser}")
    @RequiresAuth
    suspend fun delete(@Path("idRes") idRes: Long, @Path ("idUser") idUser: UUID): Response<Boolean>
}