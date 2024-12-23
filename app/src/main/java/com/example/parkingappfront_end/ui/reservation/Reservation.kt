package com.example.parkingappfront_end.ui.reservation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.LicensePlate
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.SpacesSortCriterias
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ParkingSpaceDetails(parkingSpace: ParkingSpace, sortCriteria: SpacesSortCriterias, pSpots: List<ParkingSpot>,
                        parkingViewModel: ParkingViewModel, reservationViewModel: ReservationViewModel) {
    Column(modifier= Modifier.padding(16.dp)) {
        // Titolo e distanza
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = parkingSpace.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            sortCriteria.distance?.let {
                Text(
                    text = String.format(Locale.getDefault(), "%.2f km", it),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            parkingSpace.address.street?.let {
                Text(
                    text = it,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Text(text = String.format(Locale.getDefault(), "%.0f", sortCriteria.minPrice) + "€"
                    + " - " + String.format(Locale.getDefault(), "%.0f", sortCriteria.maxPrice) + "€",
                fontSize = 18.sp
            )
        }


        Spacer(modifier = Modifier.height(8.dp)) // Aggiunto spazio

        // Sezione Privacy
        Column {


            Text(
                text = parkingSpace.address.city ?: "",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            val validSpots = parkingSpace.parkingSpots

            if (validSpots != null) {
                if (validSpots.isNotEmpty()) {
                    ParkingSpaceGrid(
                        parkingSpots = validSpots,
                        parkingViewModel = parkingViewModel,
                        reservationViewModel = reservationViewModel,
                        onParkingSpaceSelected = { selectedParkingSpot ->
                            println("Posto auto selezionato: ${selectedParkingSpot.number}")
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text= "No available parking spots.",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ParkingSpaceGrid(
    parkingSpots: List<ParkingSpot>,
    onParkingSpaceSelected: (ParkingSpot) -> Unit,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {
    val chunkedParkingSpots = parkingSpots.chunked(4) // Divide la lista in gruppi di 4 elementi (colonne)

    LaunchedEffect(key1 = parkingSpots) {
        parkingViewModel.loadLicensePlates()
    }

    Column(modifier = Modifier.padding(8.dp)) {
        chunkedParkingSpots.forEach { rowParkingSpots ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowParkingSpots.forEach { parkingSpot ->
                    ParkingSpaceItem(parkingSpot, onParkingSpaceSelected, parkingViewModel, reservationViewModel )
                }
            }
        }
    }
}


@Composable
fun ParkingSpaceItem(
    parkingSpot: ParkingSpot,
    onParkingSpaceSelected: (ParkingSpot) -> Unit,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmedReservation by remember { mutableStateOf<Reservation?>(null) }

    Log.d("ParkingSpaceItem", "Parking spot: ${parkingSpot}")
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(50.dp)
            .background(
                if (isFreeNow(parkingSpot.reservations)) Color.Green else Color.Yellow,
                shape = CircleShape
            )
            .clickable {
                if (true) { //parkingSpot.free
                    showDialog = true
                    onParkingSpaceSelected(parkingSpot)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = parkingSpot.number.toString(),
            color = Color.Black
        )
    }

    if (showDialog) {
        ReservationDialog(
            parkingSpot = parkingSpot,
            parkingViewModel = parkingViewModel,
            reservationViewModel = reservationViewModel,
            onDismiss = { showDialog = false },
            onConfirm = { reservation ->
                reservationViewModel.addReservation(reservation)
                confirmedReservation = reservation
                showConfirmationDialog = true
                showDialog = false
            }
        )
    }

    // Popup di conferma prenotazione
    confirmedReservation?.let { reservation ->
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                confirmButton = {
                    Button(onClick = { showConfirmationDialog = false }) {
                        Text("Ok")
                    }
                },
                title = { Text("Prenotazione Confermata!") },
                text = {
                    Text(
                        "Dettagli prenotazione:\n" +
                                "Posto: ${parkingSpot.number}\n" +
                                "Inizio: ${reservation.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\n" +
                                "Fine: ${reservation.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}\n" +
                                "Prezzo: €${String.format("%.2f", reservation.price)}"
                    )
                }
            )
        }
    }
}



@Composable
fun ReservationDialog(
    parkingSpot: ParkingSpot,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {

    var startDate by remember { mutableStateOf(Instant.now().atZone(ZoneId.of("Europe/Rome")).plusMinutes(10).toLocalDateTime()   ) }
    var endDate by remember { mutableStateOf(startDate.plusHours(1)) }

    var selectedPlate by remember { mutableStateOf<LicensePlate?>(null) }

    val plates by parkingViewModel.licensePlates.collectAsState()

    val price = parkingSpot.basePrice + ( (5 * parkingSpot.basePrice)/100) * (endDate.minute - startDate.minute)

    var startDatePickerDialog by remember { mutableStateOf(false) }
    var endDatePickerDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Prenota posto auto ${parkingSpot.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Date Picker per Start Date
                Button(onClick = { startDatePickerDialog = true }) {
                    Text("Inizio: ${startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                }

                // Date Picker per End Date
                Button(onClick = { endDatePickerDialog = true }) {
                    Text("Fine: ${endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                }

                // DropdownMenu per selezionare la targa
                var expanded by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = selectedPlate?.lpNumber ?: "Seleziona targa",
                    onValueChange = { },
                    label = { Text("Targa") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Espandi")
                        }
                    },
                    readOnly = true
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    plates.forEach { plate ->
                        DropdownMenuItem(
                            text = { Text(plate.lpNumber) },
                            onClick = {
                                selectedPlate = plate
                                expanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Visualizzazione del prezzo
                Text(
                    text = "Totale: ${String.format(Locale.getDefault(), "%.2f", price)} €",
                    style= MaterialTheme.typography
                        .bodyMedium
                        .copy(fontWeight = FontWeight.Bold)
                )

                // Mostra DateTimePickerDialog per Start e End
                if (startDatePickerDialog) {
                    DateTimePickerDialog(
                        initialDateTime = startDate,
                        onDateTimeSelected = { newDateTime ->
                            startDate = newDateTime
                            startDatePickerDialog = false
                        },
                        onDismiss = { startDatePickerDialog = false }
                    )
                }
                if (endDatePickerDialog) {
                    DateTimePickerDialog(
                        initialDateTime = endDate,
                        onDateTimeSelected = { newDateTime ->
                            endDate = newDateTime
                            endDatePickerDialog = false
                        },
                        onDismiss = { endDatePickerDialog = false }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val reservation = Reservation(
                    id = null, // Sostituisci con l'ID della prenotazione
                    user = SessionManager.user!!,
                    price = price,
                    parkingSpotId = parkingSpot.id,
                    licensePlateId = selectedPlate?.id ?: -1L, // Gestione ID della targa
                    startDate = startDate,
                    endDate = endDate,
                )
                onConfirm(reservation)
                onDismiss()
            }) {
                Text("Conferma")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}

@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    // Recupera il contesto Android
    val context = LocalContext.current


    // Usa il fuso orario di Roma
    val romeZone = ZoneId.of("Europe/Rome")
    val initialZonedDateTime = initialDateTime.atZone(romeZone)

    // Recupera anno, mese, giorno, ora e minuto dal valore iniziale
    val initialYear = initialZonedDateTime.year
    val initialMonth = initialZonedDateTime.monthValue - 1 // Calendar usa mesi 0-based
    val initialDay = initialZonedDateTime.dayOfMonth
    val initialHour = initialZonedDateTime.hour
    val initialMinute = initialZonedDateTime.minute

    // Mostra il DatePickerDialog
    DisposableEffect(Unit) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Aggiorna la data selezionata
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

                // Mostra il TimePickerDialog dopo la selezione della data
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        // Combina data e ora selezionate
                        val selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, minute))
                        onDateTimeSelected(selectedDateTime)
                    },
                    initialHour,
                    initialMinute,
                    true // Usa formato 24 ore
                )
                timePickerDialog.show()
            },
            initialYear,
            initialMonth,
            initialDay
        )

        // Imposta il limite minimo alla data corrente
        datePickerDialog.datePicker.minDate = Instant.now().atZone(romeZone).toEpochSecond() * 1000

        datePickerDialog.setOnDismissListener { onDismiss() }
        datePickerDialog.show()

        onDispose {
            // Chiudi i dialog quando il composable viene rimosso
            datePickerDialog.dismiss()
        }
    }
}




fun isFreeNow(reservations: List<Reservation>): Boolean {
    val now = Instant.now().atZone(ZoneId.of("Europe/Rome"))

    if (reservations.isEmpty()) {
        Log.d("ParkingSpaceItem", "Parking spot is free in this moment")
        return true // Il posto auto è libero in questo momento
    }
    Log.d("ParkingSpaceItem", "Checking reservations")
    for (reservation in reservations) {
        Log.d(
            "ParkingSpaceItem",
            "now: (${now.toString()}), startDate (${reservation.startDate}), endDate (${reservation.endDate})"
        )
        // Confronta gli orari
        val startDate = reservation.startDate.atZone(ZoneId.of("Europe/Rome"))
        val endDate = reservation.endDate.atZone(ZoneId.of("Europe/Rome"))


        if ((now.isAfter(startDate) || now.isEqual(startDate)) &&
            (now.isBefore(endDate) || now.isEqual(endDate))
        ) {
            Log.d("ParkingSpaceItem", "Parking spot is not free in this moment")
            return false // Il posto auto è occupato
        }
    }
    return true // Il posto auto è libero
}

fun isTimeSlotFree(selectedDateTime: LocalDateTime, reservations: List<Reservation>): Boolean {
    for (reservation in reservations) {
        if (selectedDateTime.isAfter(reservation.startDate) && selectedDateTime.isBefore(reservation.endDate)) {
            return false // La fascia oraria è occupata
        }
    }
    return true // La fascia oraria è libera
}
