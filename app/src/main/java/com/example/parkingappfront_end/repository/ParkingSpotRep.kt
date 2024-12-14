package com.example.parkingappfront_end.repository

import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.network.ParkingSpotApiService
import java.util.UUID

class ParkingSpotRep (private val pSpotApiService: ParkingSpotApiService) {

    suspend fun getBySpaceId(idSpace: Long, idUser: UUID) = pSpotApiService.getBySpaceId(idSpace = idSpace, idUser = idUser)

    suspend fun addParkingSpot(parkingSpot: ParkingSpot, idUser: UUID) = pSpotApiService.addParkingSpot(parkingSpot = parkingSpot, idUser = idUser)

}