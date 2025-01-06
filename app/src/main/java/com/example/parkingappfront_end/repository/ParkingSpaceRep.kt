package com.example.parkingappfront_end.repository

import android.util.Log
import com.example.parkingappfront_end.model.Address
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

    suspend fun getParkingSpacesBySearch(city: String, startDate: String, endDate: String): List<ParkingSpace> {
        return try {
            val response = pSpaceApiService.getParkingSpacesBySearch(city, startDate, endDate)
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

    suspend fun getAllAddresses(): List<Address> {
        return try {
            val response = pSpaceApiService.getAllAddresses()
            if (response.isEmpty()){
                println("No addresses found")
            }
            else {
                println("Addresses found")
            }
            response
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getParkingSpacesByOwner(id: UUID): List<ParkingSpace> {
        return try {
            val response = pSpaceApiService.getParkingSpaceByUser(id)
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


    suspend fun addParkingSpace(parkingSpace: ParkingSpace): ParkingSpace? {
        return pSpaceApiService.add(parkingSpace).body()
    }




}