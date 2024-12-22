package com.example.parkingappfront_end.repository

import android.util.Log
import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.network.ParkingSpotApiService
import java.util.UUID

class ParkingSpotRep (private val pSpotApiService: ParkingSpotApiService) {

    suspend fun getBySpaceId(idSpace: Long, idUser: UUID): List<ParkingSpot> {
        return try {
            val response = pSpotApiService.getBySpaceId(idSpace = idSpace, idUser = idUser)
            if (response.isSuccessful) {
                Log.d("ParkingSpotRep", "Response body is not empty")
            } else {
                Log.d("ParkingSpotRep", "Response body is empty")
            }
            response.body()!!
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addParkingSpot(parkingSpot: ParkingSpot, idUser: UUID): ParkingSpot {
        return pSpotApiService.addParkingSpot(parkingSpot, idUser).body()!!
    }

    suspend fun getLicensePlates(idUser: UUID): List<LicensePlate> {
        return try {
            val response = pSpotApiService.getLicensePlates(idUser)
            if (response.isEmpty()) {
                Log.d("ParkingSpotRep", "Response body is empty")
            }
            else {
                Log.d("ParkingSpotRep", "Response body is not empty")
            }
            response
        } catch (e: Exception) {
            emptyList()
        }
    }


}