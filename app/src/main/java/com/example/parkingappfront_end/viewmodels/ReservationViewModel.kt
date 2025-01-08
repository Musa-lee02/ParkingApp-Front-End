package com.example.parkingappfront_end.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.Statistic
import com.example.parkingappfront_end.repository.ParkingSpaceRep
import com.example.parkingappfront_end.repository.ParkingSpotRep
import com.example.parkingappfront_end.repository.ReservationRep
import com.example.parkingappfront_end.ui.reservation.FilterType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ReservationViewModel(private val reservationRep: ReservationRep) : ViewModel() {

    private val _myReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val myReservations: StateFlow<List<Reservation>> = _myReservations.asStateFlow()

    private val _timelyStats = MutableStateFlow<HashMap<String, Double>>(HashMap())
    val timelyStats: StateFlow<HashMap<String, Double>> = _timelyStats.asStateFlow()

    private val _weeklyStats = MutableStateFlow<HashMap<String, Double>>(HashMap())
    val weeklyStats: StateFlow<HashMap<String, Double>> = _weeklyStats.asStateFlow()

    private val _monthlyStats = MutableStateFlow<HashMap<String, Double>>(HashMap())
    val monthlyStats: StateFlow<HashMap<String, Double>> = _monthlyStats.asStateFlow()

    private val _allReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val allReservations: StateFlow<List<Reservation>> = _allReservations.asStateFlow()

    private val _filteredStats = MutableStateFlow<Map<LocalDate, Double>>(emptyMap())
    val filteredStats: StateFlow<Map<LocalDate, Double>> = _filteredStats.asStateFlow()

    fun filterStatsByDate(parkingSpace: ParkingSpace, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            try {
                val startDateTime = startDate.atStartOfDay()
                val endDateTime = endDate.atTime(LocalTime.MAX)

                val statsMap = mutableMapOf<LocalDate, Double>()

                Log.d("ReservationViewModel", "ParkingSpots: ${parkingSpace.parkingSpots?.size}")

                parkingSpace.parkingSpots?.forEach { spot ->
                    Log.d("ReservationViewModel", "Spot Reservations: ${spot.reservations.size}")
                    spot.reservations
                        .filter { it.startDate >= startDateTime && it.endDate <= endDateTime }
                        .forEach { reservation ->
                            Log.d("ReservationViewModel", "Reservation price: ${reservation.price}")
                            val date = reservation.startDate.toLocalDate()
                            val currentTotal = statsMap.getOrDefault(date, 0.0)
                            val newTotal = currentTotal + reservation.price
                            statsMap[date] = newTotal
                            Log.d("ReservationViewModel", "Date: $date, Current: $currentTotal, New: $newTotal")
                        }
                }

                _filteredStats.value = fillMissingDates(statsMap.toSortedMap(), startDate, endDate)
                Log.d("ReservationViewModel", "Last Filtered Stats: ${_filteredStats.value}")
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Error: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    private fun fillMissingDates(
        statsMap: Map<LocalDate, Double>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Double> {
        val filledMap = mutableMapOf<LocalDate, Double>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            filledMap[currentDate] = statsMap[currentDate] ?: 0.0
            currentDate = currentDate.plusDays(1)
        }

        return filledMap.toSortedMap()
    }

    fun generateLabels(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val labels = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            labels.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return labels
    }

    // Funzione per creare un mappa delle statistiche per il grafico
    fun getStatsForChart(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Double> {
        val labels = generateLabels(startDate, endDate)
        val stats = _filteredStats.value
        return labels.associateWith { date -> stats[date] ?: 0.0 }
    }

    fun loadMyReservations() {
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


    fun addReservation(reservation: Reservation) {
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


    fun calculateSalesTimely(parkingSpace: ParkingSpace) {
        _allReservations.value =
            parkingSpace.parkingSpots?.map { it.reservations }?.flatten() ?: emptyList()

        for (res in _allReservations.value) {
            if (res.startDate != null && res.endDate != null) {
                val startHour = res.startDate.hour
                val endHour = res.endDate.hour
                for (i in startHour until endHour) {
                    val key = if (i < 10) "0$i:00" else "$i:00"
                    _timelyStats.value[key] = _timelyStats.value[key]?.plus(res.price) ?: res.price
                }
            }
        }
    }

    fun calculateSalesWeekly(parkingSpace: ParkingSpace) {
        _allReservations.value =
            parkingSpace.parkingSpots?.map { it.reservations }?.flatten() ?: emptyList()
        for (res in _allReservations.value) {
            if (res.startDate != null && res.endDate != null) {
                val startDay = res.startDate.dayOfWeek
                val endDay = res.endDate.dayOfWeek

                for (i in startDay.value..endDay.value) {
                    val key = when (i) {
                        1 -> "Lunedì"
                        2 -> "Martedì"
                        3 -> "Mercoledì"
                        4 -> "Giovedì"
                        5 -> "Venerdì"
                        6 -> "Sabato"
                        7 -> "Domenica"
                        else -> ""
                    }
                    if (key.isNotEmpty()) {
                        _weeklyStats.value[key] =
                            _weeklyStats.value[key]?.plus(res.price) ?: res.price
                    }
                }
            }
        }
    }

    fun calculateSalesMonthly(parkingSpace: ParkingSpace) {
        _allReservations.value =
            parkingSpace.parkingSpots?.map { it.reservations }?.flatten() ?: emptyList()

        for (res in _allReservations.value) {
            if (res.startDate != null && res.endDate != null) {
                val startMonth = res.startDate.month
                val endMonth = res.endDate.month

                // Usa ..= per includere l'ultimo mese
                for (i in startMonth.value..endMonth.value) {
                    val key = when (i) {
                        1 -> "Gennaio"
                        2 -> "Febbraio"
                        3 -> "Marzo"
                        4 -> "Aprile"
                        5 -> "Maggio"
                        6 -> "Giugno"
                        7 -> "Luglio"
                        8 -> "Agosto"
                        9 -> "Settembre"
                        10 -> "Ottobre"
                        11 -> "Novembre"
                        12 -> "Dicembre"
                        else -> ""
                    }
                    if (key.isNotEmpty()) {
                        _monthlyStats.value[key] =
                            _monthlyStats.value[key]?.plus(res.price) ?: res.price
                    }
                }
            }
        }
    }
}