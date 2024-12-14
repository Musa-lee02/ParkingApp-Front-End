package com.example.parkingappfront_end.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import com.example.parkingappfront_end.repository.ReservationRep
import kotlinx.coroutines.launch

class ReservationViewModel(private val reservationRep : ReservationRep): ViewModel() {


    private val _myReservations = MutableLiveData<List<Reservation>>()
    val myReservations : LiveData<List<Reservation>> = _myReservations

    fun loadMyReservations(){
        viewModelScope.launch {
            try {
                val response = reservationRep.getByUser(SessionManager.user!!.id)
                if (response.isSuccessful) {
                    _myReservations.value = response.body()!!
                } else {
                    _myReservations.value = emptyList()
                }
            } catch (e: Exception) {
                _myReservations.value = emptyList()
            }
        }
    }


    fun addReservation(reservation: Reservation){
        viewModelScope.launch {
            try {
                val response = reservationRep.addReservation(SessionManager.user!!.id, reservation)
                if (response.isSuccessful) {
                    loadMyReservations()
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }



}