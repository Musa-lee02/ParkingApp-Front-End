package com.example.parkingappfront_end.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.ReservationWithDetails
import com.example.parkingappfront_end.model.domain.ReservationNotificationWorker
import com.example.parkingappfront_end.repository.ReservationRep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ReservationViewModel(private val reservationRep: ReservationRep, private val context: Context,) : ViewModel() {

    private val _myReservations = MutableStateFlow<List<ReservationWithDetails>>(emptyList())
    val myReservations: StateFlow<List<ReservationWithDetails>> = _myReservations.asStateFlow()

    private val _filteredStats = MutableStateFlow<Map<LocalDate, Double>>(emptyMap())
    val filteredStats: StateFlow<Map<LocalDate, Double>> = _filteredStats.asStateFlow()

    // Filtra le statistiche per intervallo di date
    fun filterStatsByDate(parkingSpace: ParkingSpace, startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            try {
                val startDateTime = startDate.atStartOfDay()
                val endDateTime = endDate.atTime(LocalTime.MAX)

                val statsMap = parkingSpace.parkingSpots?.flatMap { spot ->
                    spot.reservations.orEmpty()
                        .filter { it.startDate >= startDateTime && it.endDate <= endDateTime }
                        .map { it.startDate.toLocalDate() to it.price }
                }?.groupBy({ it.first }, { it.second })
                    ?.mapValues { (_, prices) -> prices.sum() }
                    ?: emptyMap()

                _filteredStats.value = fillMissingDates(statsMap, startDate, endDate)
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Errore durante il filtraggio: ${e.message}", e)
            }
        }
    }

    // Riempi date mancanti con valore 0.0
    private fun fillMissingDates(
        statsMap: Map<LocalDate, Double>,
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Double> {
        return (startDate..endDate).associateWith { statsMap[it] ?: 0.0 }
    }

    // Estrai statistiche per il grafico
    fun getStatsForChart(startDate: LocalDate, endDate: LocalDate): Map<LocalDate, Double> {
        val labels = generateLabels(startDate, endDate)
        val stats = _filteredStats.value
        return labels.associateWith { stats[it] ?: 0.0 }
    }

    // Genera etichette per le date
    fun generateLabels(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        return (startDate..endDate).toList()
    }

    // Carica le prenotazioni dell'utente
    /*
    fun loadMyReservations() {
        viewModelScope.launch {
            try {
                val response = reservationRep.getByUser(SessionManager.user!!.id)
                _myReservations.value = if (response.isSuccessful) {
                    response.body()?.sortedByDescending { it.startDate } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Errore nel caricamento: ${e.message}", e)
                _myReservations.value = emptyList()
            }
        }
    }*/

    fun loadReservationsWithDetails() {
        viewModelScope.launch {
            try {
                val response = reservationRep.getWithDetails(SessionManager.user!!.id)
                _myReservations.value = if (response.isSuccessful) {
                    response.body()?.sortedByDescending { it.startDate }?.also { reservations ->
                        // Schedula notifiche per tutte le prenotazioni future
                        reservations.forEach { reservation ->
                            if (reservation.startDate.isAfter(LocalDateTime.now())) {
                                scheduleReservationNotification(reservation)
                            }
                        }
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Errore nel caricamento: ${e.message}", e)
                _myReservations.value = emptyList()
            }
        }
    }

    // Aggiungi una prenotazione
    fun addReservation(reservation: Reservation) {
        viewModelScope.launch {
            try {
                val response = reservationRep.addReservation(SessionManager.user!!.id, reservation)
                if (response.isSuccessful) {
                    loadReservationsWithDetails()
                }
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Errore nell'aggiunta: ${e.message}", e)
            }
        }
    }

    // Elimina una prenotazione
    fun deleteReservation(idRes: Long) {
        viewModelScope.launch {
            try {
                val response = reservationRep.deleteReservation(idRes, SessionManager.user!!.id)
                if (response.isSuccessful) {
                    loadReservationsWithDetails()
                }
            } catch (e: Exception) {
                Log.e("ReservationViewModel", "Errore nell'eliminazione: ${e.message}", e)
            }
        }
    }

    // Estensione per generare un intervallo di date
    private operator fun LocalDate.rangeTo(other: LocalDate): Sequence<LocalDate> = sequence {
        var current = this@rangeTo
        while (!current.isAfter(other)) {
            yield(current)
            current = current.plusDays(1)
        }
    }

    fun scheduleReservationNotification(reservation: ReservationWithDetails) {
        reservation.id?.let {
            ReservationNotificationWorker.scheduleNotification(
                context =  context,
                reservationId = it,
                startDate = reservation.startDate,
                spaceName = reservation.spaceName ?: "parcheggio"
            )
        }
    }


}