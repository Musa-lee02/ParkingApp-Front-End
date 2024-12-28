package com.example.parkingappfront_end.network

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

    @GET("parkingSpaces/getBySearch/{city}/{startDate}/{endDate}")
    @RequiresAuth
    suspend fun getParkingSpacesBySearch(@Path("city") city: String, @Path("startDate") startDate: String, @Path("endDate") endDate: String): List<ParkingSpace>

    @GET("parkingSpaces/getByUser/{idUser}")
    @RequiresAuth
    suspend fun getParkingSpaceByUser(@Path("idUser") idUser: UUID): List<ParkingSpace>

    @POST("parkingSpaces/add")
    @RequiresAuth
    suspend fun add(@Body parkingSpace: ParkingSpace): Response<ParkingSpace>

    @POST("parkingSpots/addReservation/{idUser}")
    @RequiresAuth
    suspend fun addReservation(@Body reservation: Reservation, @Path("idUser") idUser: UUID): Response<Reservation>



}
