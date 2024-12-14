package com.example.parkingappfront_end.repository

import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.network.ReservationApiService
import java.util.UUID

class ReservationRep (private val reservationApiService: ReservationApiService){

    suspend fun getAll() = reservationApiService.getAll()

    suspend fun getByUser(idUser: UUID) = reservationApiService.getById(idUser)

    suspend fun getBySpot(idSpot: Long) = reservationApiService.getBySpot(idSpot)

    suspend fun addReservation(idUser: UUID, reservation: Reservation) = reservationApiService.add(reservation, idUser)


}