package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.RequiresAuth
import com.example.parkingappfront_end.model.Reservation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ParkingSpaceApiService {

    @GET("parkingSpaces/getAll")
    @RequiresAuth
    suspend fun getAllParkingSpaces(): List<ParkingSpace>

    @GET("parkingSpaces/getByUser/{idUser}")
    @RequiresAuth
    suspend fun getParkingSpaceByUser(@Path("idUser") idUser: UUID): List<ParkingSpace>

    @GET("parkingSpaces/getLicensePlates/{idUser}")
    @RequiresAuth
    suspend fun getLicensePlates(@Path("idUser") idUser: UUID): List<LicensePlate>


    @POST("parkingSpaces/add")
    @RequiresAuth
    suspend fun add(@Body parkingSpace: ParkingSpace): Response<ParkingSpace>

    @POST("parkingSpots/addReservation/{idUser}")
    @RequiresAuth
    suspend fun addReservation(@Body reservation: Reservation, @Path("idUser") idUser: UUID): Response<Reservation>



}
