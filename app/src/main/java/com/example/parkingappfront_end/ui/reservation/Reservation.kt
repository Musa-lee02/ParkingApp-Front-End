package com.example.parkingappfront_end.ui.reservation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.SearchParams
import com.example.parkingappfront_end.model.SpacesSortCriterias
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


@Composable
fun MainSearchResults(
    navController: NavController,
    viewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
    searchCriteria: SearchParams?
) {
    val sortedPS by viewModel.sortedPSBy.collectAsState()
    val selectedParkingSpace = remember { mutableStateOf<ParkingSpace?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(sortedPS) {
        if (sortedPS.isNotEmpty()) {
            selectedParkingSpace.value = selectedParkingSpace.value ?: sortedPS.first().first
        } else {
            if (searchCriteria != null) {
                viewModel.loadParkingSpacesBySearch(
                    searchCriteria.city,
                    searchCriteria.startDate.toString(),
                    searchCriteria.endDate.toString()
                )
            }
            else if (SessionManager.user?.role == "ROLE_OWNER") {
                viewModel.loadParkingSpacesByOwner()
            }

        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            ParkingSpaceList(
                selectedParkingSpace = selectedParkingSpace.value,
                onParkingSpaceSelected = { parkingSpace ->
                    selectedParkingSpace.value = parkingSpace
                },
                viewModel = viewModel
            )

            selectedParkingSpace.value?.let { parkingSpace ->
                val selectedPair = sortedPS.firstOrNull { it.first == parkingSpace }
                selectedPair?.let {
                    if (searchCriteria != null) {
                        ParkingSpaceDetails(
                            parkingSpace = it.first,
                            searchCriteria = searchCriteria,
                            sortCriteria = it.second,
                            parkingViewModel = viewModel,
                            reservationViewModel = reservationViewModel
                        )
                    }
                    else if (SessionManager.user?.role == "ROLE_OWNER") {
                        ParkingSpaceDetails(
                            parkingSpace = it.first,
                            searchCriteria = SearchParams( "", LocalDateTime.now(), LocalDateTime.now()),
                            sortCriteria = it.second,
                            parkingViewModel = viewModel,
                            reservationViewModel = reservationViewModel
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun ParkingSpaceDetails(
    parkingSpace: ParkingSpace,
    searchCriteria: SearchParams,
    sortCriteria: SpacesSortCriterias,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {

    val isSortedByDistance by parkingViewModel.isSortedByDistance.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Nome e distanza
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                    fontWeight = if (isSortedByDistance) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSortedByDistance) Color.Blue else Color.Unspecified
                )
            }
        }

        // Via e prezzo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            parkingSpace.address.street?.let {
                Text(
                    text = it,
                    fontSize = 18.sp
                )
            }
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%.0f€ - %.0f€",
                    sortCriteria.minPrice,
                    sortCriteria.maxPrice
                ),
                fontSize = 18.sp,
                fontWeight = if (!isSortedByDistance) FontWeight.Bold else FontWeight.Normal,
                color = if (!isSortedByDistance) Color.Blue else Color.Unspecified
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parkingSpace.address.city ?: "",
                fontSize = 18.sp
            )

            Text(
                text = parkingSpace.parkingSpots?.size.toString() + " posti",
                fontSize = 18.sp,
                fontWeight = if (!isSortedByDistance) FontWeight.Bold else FontWeight.Normal,
                color = if (!isSortedByDistance) Color.Blue else Color.Unspecified
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Lista posti auto

        Log.d("ParkingSpaceDetails", "ParkingSpots: ${parkingSpace.parkingSpots}")
        parkingSpace.parkingSpots?.let { spots ->
            if (spots.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(spots) { spot ->
                            ParkingSpaceListItem(
                                parkingSpot = spot,
                                onParkingSpaceSelected = {},
                                searchCriteria = searchCriteria,
                                parkingViewModel = parkingViewModel,
                                reservationViewModel = reservationViewModel
                            )

                        }
                    }
                }
            } else {
                Text(
                    text = "Nessun posto disponibile",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Composable
fun ParkingSpaceList(
    selectedParkingSpace: ParkingSpace?,
    viewModel: ParkingViewModel,
    onParkingSpaceSelected: (ParkingSpace) -> Unit
) {

    var showAddParkingSpace by remember {
        mutableStateOf(false)
    }
    val sortByDistance by viewModel.isSortedByDistance.collectAsState()
    val sortedPS by viewModel.sortedPSBy.collectAsState()


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (SessionManager.user?.role == "ROLE_OWNER") "I tuoi parcheggi" else "Risultati ricerca",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        if (SessionManager.user?.role == "ROLE_OWNER") {
            IconButton(
                onClick = {
                    showAddParkingSpace = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = if (sortByDistance) "Ordina per prezzo" else "Ordina per distanza"
                )
            }

        }
        else{
            IconButton(
                onClick = {
                    if (sortByDistance) {
                        viewModel.sortPSByPrice()
                    } else {
                        viewModel.sortPSByDistance()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = if (sortByDistance) "Ordina per prezzo" else "Ordina per distanza"
                )
            }
        }
    }

    if (showAddParkingSpace) {

        AddParkingSpaceDialog(
            onDismissRequest = { showAddParkingSpace = false },
            onAddSpace = { name, city, street ->
                viewModel.addParkingSpace(name, city, street)
                showAddParkingSpace = false
            }
        )

    }

    Log.d("ParkingSpaces", "Reservation: ${sortedPS.firstOrNull()?.first}")
    if (sortedPS.isEmpty()) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessun parcheggio disponibile",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(
                items = sortedPS,
            ) { parkingSpace ->

                ParkingSpaceThumbnail(
                    parkingSpace = parkingSpace.first,
                    onClick = {
                        onParkingSpaceSelected(parkingSpace.first)
                    },
                    isSelected = selectedParkingSpace == parkingSpace.first
                )
            }
        }
    }
}

@Composable
fun ParkingSpaceThumbnail(parkingSpace: ParkingSpace, onClick: () -> Unit, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)  Color.White else Color(0xFFE0E0E0)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = parkingSpace.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@Composable
fun ParkingSpaceListItem(
    parkingSpot: ParkingSpot,
    searchCriteria: SearchParams,
    onParkingSpaceSelected: (ParkingSpot) -> Unit,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmedReservation by remember { mutableStateOf<Reservation?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Numero del posto
                Text(
                    text = "Posto Auto #${parkingSpot.number}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Prezzo
                Text(text = "€${String.format("%.2f", calculatePrice(searchCriteria.startDate, searchCriteria.endDate, parkingSpot.basePrice))}",
                    fontSize = 16.sp
                )
            }

            if (SessionManager.user?.role == "ROLE_OWNER" || SessionManager.user?.role == "ROLE_ADMIN") {
                IconButton(
                    onClick = {
                        parkingViewModel.deleteParkingSpot(parkingSpot.id)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Elimina",
                        tint = Color.Red
                    )
                }
            }
            else {
                IconButton(
                    onClick = {
                        onParkingSpaceSelected(parkingSpot)
                        showDialog = true
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Acquista",
                        tint = Color.Green
                    )
                }
            }
        }
    }

    // Mostra il dialogo di prenotazione quando il posto viene selezionato
    if (showDialog) {
        ReservationDialog(
            parkingSpot = parkingSpot,
            searchCriteria = searchCriteria,
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
    searchCriteria: SearchParams,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {
    var price = calculatePrice(searchCriteria.startDate, searchCriteria.endDate, parkingSpot.basePrice)
    var licensePlate by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Conferma Prenotazione",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Posto #${parkingSpot.number}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date e orari
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateTimeRow(
                            label = "Check-in",
                            date = searchCriteria.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            time = searchCriteria.startDate.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                        Divider()

                        DateTimeRow(
                            label = "Check-out",
                            date = searchCriteria.endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            time = searchCriteria.endDate.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                }

                // Targa
                OutlinedTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = { Text("Targa veicolo") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )

                // Prezzo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Totale", fontSize = 16.sp)
                        Text(
                            "€${String.format("%.2f", price)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reservation = Reservation(
                        id = null,
                        user = SessionManager.user!!,
                        price = price,
                        parkingSpotId = parkingSpot.id,
                        licensePlate = licensePlate.text,
                        startDate = searchCriteria.startDate,
                        endDate = searchCriteria.endDate,
                    )
                    onConfirm(reservation)
                },
                enabled = licensePlate.text.isNotEmpty() && licensePlate.text.length >= 6,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Conferma prenotazione")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Annulla")
            }
        }
    )
}

@Composable
private fun DateTimeRow(
    label: String,
    date: String,
    time: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = date,
                fontSize = 16.sp
            )
            Text(
                text = time,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


fun calculatePrice(startDate: LocalDateTime, endDate: LocalDateTime, basePrice: Double): Double {
    val minutes = ChronoUnit.MINUTES.between(startDate, endDate)

    val finalPrice = basePrice + minutes * (0.01 * basePrice)
    return  finalPrice
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
