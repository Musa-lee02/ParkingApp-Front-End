package com.example.parkingappfront_end.viewmodels

import android.util.Log
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.Address
import com.example.parkingappfront_end.model.SpacesSortCriterias
import com.example.parkingappfront_end.model.UserId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class ParkingViewModel(private val parkingSpaceRep : ParkingSpaceRep, private val parkingSpotRep: ParkingSpotRep): ViewModel() {

    private val _parkingSpaces = MutableStateFlow<List<ParkingSpace>>(emptyList())
    val parkingSpaces: StateFlow<List<ParkingSpace>> = _parkingSpaces.asStateFlow()

    private val _sortedPSBy = MutableStateFlow<List<Pair<ParkingSpace, SpacesSortCriterias>>>(emptyList())
    val sortedPSBy: StateFlow<List<Pair<ParkingSpace,SpacesSortCriterias>>> = _sortedPSBy.asStateFlow()

    private val _isSortedByDistance = MutableStateFlow<Boolean>(false)
    val isSortedByDistance: StateFlow<Boolean> = _isSortedByDistance.asStateFlow()

    private val _isSortedByPrice = MutableStateFlow<Boolean>(false)
    val sortedByPrice: StateFlow<Boolean> = _isSortedByPrice.asStateFlow()

    private val _allAddresses = MutableStateFlow<List<Address>>(emptyList())
    val allAddresses: StateFlow<List<Address>> = _allAddresses.asStateFlow()

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun loadAddresses() {
        viewModelScope.launch {
            try {
                val addresses = parkingSpaceRep.getAllAddresses()
                _allAddresses.value = addresses
                Log.d("ParkingSpaces", "View Model Addresses loaded: $addresses")
            } catch (e: Exception) {
                _allAddresses.value = emptyList()
                Log.e("ParkingSpaces", "Error loading addresses", e)
            }
        }
    }

    fun loadParkingSpacesByOwner() {
        viewModelScope.launch {
            try {
                _isLoading .value = true
                val spaces = parkingSpaceRep.getParkingSpacesByOwner(SessionManager.user!!.id)
                _parkingSpaces.value = spaces
                Log.d("ParkingSpaces", "View Model Parking spaces loaded: $spaces")

                if (SessionManager.position != null) {
                    assignCriterias()
                    sortPSByDistance()
                    Log.d("ParkingSpaces", "Criterias assigned")
                    Log.d("ParkingSpaces", "Parking spaces sorted with distances: ${_sortedPSBy.value}")
                }
                _isLoading.value = false

            } catch (e: Exception) {
                _allAddresses.value = emptyList()
                Log.e("ParkingSpaces", "Error loading addresses", e)
            }
        }

    }

    fun loadParkingSpacesBySearch(city: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val spaces = parkingSpaceRep.getParkingSpacesBySearch(city, startDate, endDate)
                _parkingSpaces.value = spaces
                Log.d("ParkingSpaces", "View Model Parking spaces loaded: $spaces")
                // Calcola le distanze dopo aver caricato i parcheggi
                if (SessionManager.position != null) {
                    assignCriterias()
                    sortPSByDistance()
                    Log.d("ParkingSpaces", "Criterias assigned")
                    Log.d("ParkingSpaces", "Parking spaces sorted with distances: ${_sortedPSBy.value}")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _parkingSpaces.value = emptyList()
                Log.e("ParkingSpaces", "Error loading parking spaces", e)
                _isLoading.value = false
            }
        }
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

    fun addParkingSpace(name: String, city: String, street: String) {
        viewModelScope.launch {
            try {
                val address = Address(city = city, street = street, latitude = Random.nextDouble(39.000,42.000), longitude = Random.nextDouble(12.000, 15.000))
                val parkingSpace = ParkingSpace(name = name, address = address,
                        userId = UserId(userId = SessionManager.user!!.id))

                val newSpace = parkingSpaceRep.addParkingSpace(parkingSpace)
                if (newSpace != null) {
                    loadParkingSpacesByOwner()
                    Log.d("ParkingSpaces", "Parking space added: $newSpace")
                }
                Log.d("ParkingSpaces", "Parking space added: $newSpace")
            } catch (e: Exception) {
                Log.e("ParkingSpaces", "Error adding parking space", e)
            }
        }

    }

    fun deleteParkingSpot(id: Long) {
        viewModelScope.launch {
            try {
                val resp = parkingSpotRep.deleteParkingSpot(id)
                if (resp == true) {
                    _parkingSpots.value = _parkingSpots.value.filter { it.id != id }
                    Log.d("ParkingSpaces", "Parking spot deleted: $id")
                }
                else {
                    Log.d("ParkingSpaces", "Error deleting parking spot")
                }
            } catch (e: Exception) {
                Log.e("ParkingSpaces", "Error deleting parking spot", e)
            }
        }

    }


    fun assignCriterias(){
        viewModelScope.launch {
            val sorted = _parkingSpaces.value.map { parkingSpace ->
                var minPrice = 0.0
                var maxPrice = 0.0
                if (parkingSpace.parkingSpots?.isNotEmpty() == true){
                    minPrice =  minPrice(parkingSpace)
                    maxPrice = maxPrice(parkingSpace)
                }
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
            _isSortedByDistance.value = false
            _isSortedByPrice.value = true
            Log.d("ParkingSpaces", "Parking spaces sorted with prices: $sorted")
        }
    }

    fun sortPSByDistance() {
        viewModelScope.launch {
            val sorted = _sortedPSBy.value.sortedBy { it.second.distance }
            _sortedPSBy.value = sorted
            _isSortedByDistance.value = true
            _isSortedByPrice.value = false
            Log.d("ParkingSpaces", "Parking spaces sorted with distances: $sorted")
        }
    }

    fun minPrice(parkingSpace: ParkingSpace): Double {
        var min = parkingSpace.parkingSpots?.get(0)?.basePrice
        parkingSpace.parkingSpots?.forEach {
            if (it.basePrice <= (min ?: 500.0)) {
                min = it.basePrice
            }
        }
        return min ?: 0.0
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

}