package com.example.parkingappfront_end.viewmodels

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import java.util.UUID
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(private val parkingSpaceRep : ParkingSpaceRep, private val parkingSpotRep: ParkingSpotRep): ViewModel() {

    private val _parkingSpaces = MutableStateFlow<List<ParkingSpace>>(emptyList())
    val parkingSpaces: StateFlow<List<ParkingSpace>> = _parkingSpaces.asStateFlow()

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _licensePlates = MutableStateFlow<List<LicensePlate>>(emptyList())
    val licensePlates: StateFlow<List<LicensePlate>> = _licensePlates.asStateFlow()

    fun loadParkingSpaces(){
        viewModelScope.launch {
            try {
                _parkingSpaces.value = parkingSpaceRep.getAllPS()
                Log.d("Parking", "View Model Parking spaces loaded: ${_parkingSpaces.value}")

            } catch (e: Exception) {
                _parkingSpaces.value = emptyList()
            }

        }
    }

    fun loadParkingSpots(idSpace: Long){
        viewModelScope.launch {
            try {
                val response = parkingSpotRep.getBySpaceId(idSpace, SessionManager.user!!.id)
                _parkingSpots.value = response

            } catch (e: Exception) {
                println(e)
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

    fun addReservation(reservation: Reservation) {
        viewModelScope.launch {
            try {
                parkingSpaceRep.addReservation(reservation, SessionManager.user!!.id)
            } catch (e: Exception) {
                println(e)
            }
        }

    }


}