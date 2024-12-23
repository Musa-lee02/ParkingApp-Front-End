package com.example.parkingappfront_end.viewmodels

import android.util.Log
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.SpacesSortCriterias
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class ParkingViewModel(private val parkingSpaceRep : ParkingSpaceRep, private val parkingSpotRep: ParkingSpotRep): ViewModel() {

    private val _parkingSpaces = MutableStateFlow<List<ParkingSpace>>(emptyList())
    val parkingSpaces: StateFlow<List<ParkingSpace>> = _parkingSpaces.asStateFlow()

    private val _sortedPSBy = MutableStateFlow<List<Pair<ParkingSpace, SpacesSortCriterias>>>(emptyList())
    val sortedPSBy: StateFlow<List<Pair<ParkingSpace,SpacesSortCriterias>>> = _sortedPSBy.asStateFlow()

    private val _sortedByDistance = MutableStateFlow<Boolean>(false)
    val sortedByDistance: StateFlow<Boolean> = _sortedByDistance.asStateFlow()

    private val _sortedByPrice = MutableStateFlow<Boolean>(false)
    val sortedByPrice: StateFlow<Boolean> = _sortedByPrice.asStateFlow()

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _licensePlates = MutableStateFlow<List<LicensePlate>>(emptyList())
    val licensePlates: StateFlow<List<LicensePlate>> = _licensePlates.asStateFlow()


    fun loadParkingSpaces() {
        viewModelScope.launch {
            try {
                val spaces = parkingSpaceRep.getAllPS()
                _parkingSpaces.value = spaces
                Log.d("ParkingSpaces", "View Model Parking spaces loaded: $spaces")
                // Calcola le distanze dopo aver caricato i parcheggi
                if (SessionManager.position != null) {
                    assignCriterias()
                    sortPSByDistance()
                }
            } catch (e: Exception) {
                _parkingSpaces.value = emptyList()
                Log.e("ParkingSpaces", "Error loading parking spaces", e)
            }
        }
    }
    fun assignCriterias(){
        viewModelScope.launch {
            val sorted = _parkingSpaces.value.map { parkingSpace ->
                val minPrice =  minPrice(parkingSpace)
                val maxPrice = maxPrice(parkingSpace)
                val distance = calculateDistance(
                    SessionManager.position!!.latitude, SessionManager.position!!.longitude,
                    parkingSpace.address.latitude, parkingSpace.address.longitude
                )
                parkingSpace to SpacesSortCriterias(minPrice, maxPrice, distance)
            }
            _sortedPSBy.value = sorted
        }
    }

    fun sortPSByPrice() {
        viewModelScope.launch {
           val sorted = _sortedPSBy.value.sortedBy { it.second.minPrice }

            _sortedPSBy.value = sorted
            _sortedByDistance.value = false
            _sortedByPrice.value = true
            Log.d("ParkingSpaces", "Parking spaces sorted with prices: $sorted")
        }
    }

    fun sortPSByDistance() {
        viewModelScope.launch {
            val sorted = _sortedPSBy.value.sortedBy { it.second.distance }
            _sortedPSBy.value = sorted
            _sortedByDistance.value = true
            _sortedByPrice.value = false
            Log.d("ParkingSpaces", "Parking spaces sorted with distances: $sorted")
        }
    }

    fun minPrice(parkingSpace: ParkingSpace): Double? {
        var min = parkingSpace.parkingSpots?.get(0)?.basePrice
        parkingSpace.parkingSpots?.forEach {
            if (it.basePrice <= (min ?: 500.0)) {
                min = it.basePrice
            }
        }
        return min
    }

    fun maxPrice(parkingSpace: ParkingSpace): Double {
        var max = parkingSpace.parkingSpots?.get(0)?.basePrice
        parkingSpace.parkingSpots?.forEach {
            if (it.basePrice >= (max ?: 0.0)) {
                max = it.basePrice
            }
        }
        return max ?: 0.0
    }





    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Raggio terrestre in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c // Distanza in km
    }

    fun loadParkingSpots(idSpace: Long) {
        viewModelScope.launch {
            try {
                val spots = parkingSpotRep.getBySpaceId(idSpace, SessionManager.user!!.id)
                _parkingSpots.value = spots
                Log.d("ParkingSpaces", "Parking spots loaded: ${spots}")
            } catch (e: Exception) {
                Log.e("ParkingSpaces", "Error loading parking spots", e)
            }
        }
    }


    fun loadLicensePlates(){
        viewModelScope.launch {
            try {
                _licensePlates.value = parkingSpaceRep.getLicensePlates(SessionManager.user!!.id)
            } catch (e: Exception) {
                println(e)
            }
        }
    }




}