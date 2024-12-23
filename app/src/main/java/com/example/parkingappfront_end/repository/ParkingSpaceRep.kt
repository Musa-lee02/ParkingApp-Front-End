package com.example.parkingappfront_end.repository

import android.util.Log
import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.network.ParkingSpaceApiService
import java.util.UUID

class ParkingSpaceRep(private val pSpaceApiService : ParkingSpaceApiService) {

    suspend fun getAllPS(): List<ParkingSpace> {
        Log.d("Parking", "Calling getAllParkingSpaces()")

        return try {
            val response = pSpaceApiService.getAllParkingSpaces()
            Log.d("Parking", "Rep Parking spaces loaded: $response")
            if (response.isEmpty()){
                Log.d("Parking", "No parking spaces found")
            }
            else {
                Log.d("Parking", "Parking spaces found")
            }
            response
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getPSpaceByUser(idUser: UUID): List<ParkingSpace> {
        return try {
            val response = pSpaceApiService.getParkingSpaceByUser(idUser)
           if (response.isEmpty()){
                println("No parking spaces found")
            }
            else {
                println("Parking spaces found")
            }
            response
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getLicensePlates(idUser: UUID): List<LicensePlate> {
        return try {
            val response = pSpaceApiService.getLicensePlates(idUser)
            if (response.isEmpty()){
                println("No license plates found")
            }
            else {
                println("License plates found")
            }
            response
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addParkingSpace(parkingSpace: ParkingSpace): ParkingSpace {
        return pSpaceApiService.add(parkingSpace).body()!!
    }


}