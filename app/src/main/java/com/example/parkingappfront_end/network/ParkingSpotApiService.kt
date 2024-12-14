package com.example.parkingappfront_end.network

import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.model.RequiresAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ParkingSpotApiService {

    @GET("parkingSpots/getBySpaceId/{idSpace}/{idUser}")
    @RequiresAuth
    suspend fun getBySpaceId(@Path("idSpace") idSpace: Long, @Path("idUser") idUser: UUID): Response<List<ParkingSpot>>

    @POST("parkingSpots/add/{idUser}")
    @RequiresAuth
    suspend fun addParkingSpot(@Body parkingSpot: ParkingSpot, @Path("idUser") idUser: UUID): Response<ParkingSpot>


}