package com.example.parkingappfront_end.viewmodels

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
import kotlinx.coroutines.launch

class ParkingViewModel(private val parkingSpaceRep : ParkingSpaceRep, private val parkingSpotRep: ParkingSpotRep): ViewModel() {

    private val _parkingSpaces = MutableLiveData<List<ParkingSpace>>()
    val parkingSpaces: LiveData<List<ParkingSpace>> = _parkingSpaces

    private val _parkingSpots = MutableLiveData<List<ParkingSpot>>()
    val parkingSpots: LiveData<List<ParkingSpot>> = _parkingSpots

    fun loadParkingSpaces(){
        viewModelScope.launch {

            try {
                val response = parkingSpaceRep.getAllPS()
                if (response.isSuccessful) {
                    _parkingSpaces.value = response.body()!!
                } else {
                    _parkingSpaces.value = emptyList()
                }
            } catch (e: Exception) {
                _parkingSpaces.value = emptyList()
            }

        }
    }

    fun loadParkingSpots(idSpace: Long){
        viewModelScope.launch {
            try {
                val response = parkingSpotRep.getBySpaceId(idSpace, SessionManager.user!!.id)
                if (response.isSuccessful) {
                    _parkingSpots.value = response.body()!!
                } else {
                    _parkingSpots.value = emptyList()

                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }




}