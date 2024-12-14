package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.RequiresAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ParkingSpaceApiService {

    @GET("parkingSpaces/getAll")
    //@RequiresAuth
    suspend fun getAllParkingSpaces(): Response<List<ParkingSpace>>

    @GET("parkingSpaces/getByUser/{idUser}")
    @RequiresAuth
    suspend fun getParkingSpaceByUser(@Path("idUser") idUser: UUID): Response<List<ParkingSpace>>

    @POST("parkingSpaces/add")
    @RequiresAuth
    suspend fun add(@Body parkingSpace: ParkingSpace): Response<ParkingSpace>






}
