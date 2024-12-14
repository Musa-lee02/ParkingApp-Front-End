package com.example.parkingappfront_end.repository

import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.network.ParkingSpaceApiService
import java.util.UUID

class ParkingSpaceRep(private val pSpaceApiService : ParkingSpaceApiService) {

    suspend fun getAllPS() = pSpaceApiService.getAllParkingSpaces()

    suspend fun getPSpaceByUser(idUser: UUID) = pSpaceApiService.getParkingSpaceByUser(idUser)

    suspend fun addParkingSpace(parkingSpace: ParkingSpace) = pSpaceApiService.add(parkingSpace)


}