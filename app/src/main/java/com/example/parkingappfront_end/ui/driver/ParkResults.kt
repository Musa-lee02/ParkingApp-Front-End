package com.example.parkingappfront_end.ui.driver

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.model.domain.PaymentMethodType
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.domain.SearchParams
import com.example.parkingappfront_end.model.domain.SpacesSortCriterias
import com.example.parkingappfront_end.model.domain.SpotType
import com.example.parkingappfront_end.ui.ownerSide.AddParkingSpaceDialog
import com.example.parkingappfront_end.ui.ownerSide.AddParkingSpotDialog
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.time.LocalDateTime
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
                            reservationViewModel = reservationViewModel,
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
    var showAddSpot by remember {
        mutableStateOf(false)
    }

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
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        sortCriteria.distance?.let {
            Text(
                text = String.format(Locale.getDefault(), "%.2f km", it),
                fontSize = 20.sp,
                fontWeight = if (isSortedByDistance) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        color = if (isSortedByDistance) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp)
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
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%.0f€ - %.0f€",
                    sortCriteria.minPrice,
                    sortCriteria.maxPrice
                ),
                fontSize = 20.sp,
                fontWeight = if (!isSortedByDistance) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .background(
                        color = if (!isSortedByDistance) MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.2f
                        ) else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parkingSpace.address.city ?: "",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${parkingSpace.parkingSpots?.size ?: 0} posti",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))

                if (SessionManager.user?.role == "ROLE_OWNER") {
                    IconButton(onClick = {
                        showAddSpot = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Aggiungi posto",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista posti auto

        Log.d("ParkingSpaceDetails", "ParkingSpots: ${parkingSpace.parkingSpots}")
        parkingSpace.parkingSpots?.let { spots ->
            if (spots.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(spots) { spot ->
                            ParkingSpaceListItem(
                                parkingSpace = parkingSpace,
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
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAddSpot) {
        AddParkingSpotDialog(
            onDismissRequest = { showAddSpot = false },
            onAddSpot = { number, basePrice, type ->
                parkingSpace?.id?.let { parkingViewModel.addParkingSpot(number, basePrice, type, it) }
                showAddSpot = false
            }
        )
    }
}

@Composable
fun ParkingSpaceList(
    selectedParkingSpace: ParkingSpace?,
    viewModel: ParkingViewModel,
    onParkingSpaceSelected: (ParkingSpace) -> Unit
) {
    var showAddParkingSpace by remember { mutableStateOf(false) }
    var optionsExpanded by remember { mutableStateOf(false) }
    var onDeleteClick by remember { mutableStateOf(false) }
    val sortByDistance by viewModel.isSortedByDistance.collectAsState()
    val sortedPS by viewModel.sortedPSBy.collectAsState()

    // Header con opzioni di ordinamento o gestione parcheggi per proprietari
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (SessionManager.user?.role == "ROLE_OWNER") "I tuoi parcheggi" else "Risultati ricerca",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        if (SessionManager.user?.role == "ROLE_OWNER") {
            Box(contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = { optionsExpanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Gray)
                }
                DropdownMenu(expanded = optionsExpanded, onDismissRequest = { optionsExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Aggiungi") },
                        onClick = {
                            showAddParkingSpace = true
                            optionsExpanded = false
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Aggiungi", tint = Color.Green)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Elimina") },
                        onClick = {
                            onDeleteClick = true
                            optionsExpanded = false
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Elimina", tint = Color.Red)
                        }
                    )
                }
            }
        } else {
            IconButton(onClick = {
                if (sortByDistance) viewModel.sortPSByPrice() else viewModel.sortPSByDistance()
            }) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = if (sortByDistance) "Ordina per prezzo" else "Ordina per distanza"
                )
            }
        }
    }

    // Dialog per aggiungere un parcheggio
    if (showAddParkingSpace) {
        AddParkingSpaceDialog(
            onDismissRequest = { showAddParkingSpace = false },
            onAddSpace = { name, city, street ->
                viewModel.addParkingSpace(name, city, street)
                showAddParkingSpace = false
            }
        )
    }

    // Dialog per confermare l'eliminazione del parcheggio
    if (onDeleteClick) {
        AlertDialog(
            onDismissRequest = { onDeleteClick = false },
            title = { Text("Elimina parcheggio") },
            text = { Text("Sei sicuro di voler eliminare questo parcheggio?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedParkingSpace?.id?.let { viewModel.deleteParkingSpace(it) }
                        onDeleteClick = false
                    }
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(onClick = { onDeleteClick = false }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Visualizzazione dei parcheggi disponibili
    if (sortedPS.isEmpty()) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nessun parcheggio disponibile", fontSize = 18.sp, textAlign = TextAlign.Center)
        }
    } else {
        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            items(items = sortedPS) { parkingSpace ->
                ParkingSpaceThumbnail(
                    parkingSpace = parkingSpace.first,
                    onClick = { onParkingSpaceSelected(parkingSpace.first) },
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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)  Color.White else Color(0xFFE0E0E0)
        ),
        border = if (isSelected) BorderStroke(1.dp, Color.Blue) else null
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
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.Blue else Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@Composable
fun ParkingSpaceListItem(
    parkingSpot: ParkingSpot,
    parkingSpace: ParkingSpace,
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
            .padding(8.dp)
            .clickable { onParkingSpaceSelected(parkingSpot) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (parkingSpot.type) {
                    SpotType.CAR -> Icons.Filled.DirectionsCar
                    SpotType.MOTORCYCLE -> Icons.Filled.Motorcycle
                    else -> Icons.Filled.LocalParking
                }

                Icon(
                    imageVector = icon,
                    contentDescription = parkingSpot.type.name,
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Posto #${parkingSpot.number}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "€${String.format("%.2f", calculatePrice(searchCriteria.startDate, searchCriteria.endDate, parkingSpot.basePrice))}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (SessionManager.user?.role == "ROLE_OWNER" || SessionManager.user?.role == "ROLE_ADMIN") {
                IconButton(
                    onClick = {
                        parkingSpot.id?.let { parkingViewModel.deleteParkingSpot(it) }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error)
                }
            } else {
                IconButton(
                    onClick = {
                        onParkingSpaceSelected(parkingSpot)
                        showDialog = true
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Acquista", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showDialog) {
        ReservationDialog(
            parkingSpace = parkingSpace,
            parkingSpot = parkingSpot,
            searchCriteria = searchCriteria,
            parkingViewModel = parkingViewModel,
            reservationViewModel = reservationViewModel,
            onDismiss = { showDialog = false },
            onConfirm = { reservation ->
                confirmedReservation = reservation
                reservationViewModel.addReservation(reservation)
                showConfirmationDialog = true
                showDialog = false
            }
        )
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            parkingSpot = parkingSpot,
            parkingSpace = parkingSpace,
            reservation = confirmedReservation!!,
            onDismissRequest = { showConfirmationDialog = false }
        )
    }
}



@Composable
fun ReservationDialog(
    parkingSpace: ParkingSpace,
    parkingSpot: ParkingSpot,
    searchCriteria: SearchParams,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onConfirm: (Reservation) -> Unit
) {
    var licensePlate by remember { mutableStateOf(TextFieldValue("")) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var currentReservation by remember { mutableStateOf<Reservation?>(null) }
    var price = calculatePrice(searchCriteria.startDate, searchCriteria.endDate, parkingSpot.basePrice)

    // Dialog principale per i dettagli della prenotazione
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
                    "Parcheggio ${parkingSpace?.name}",
                    fontSize = 16.sp,
                    color = Color.Gray,
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
                // Date e orari (codice esistente)
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

                // Campo targa (codice esistente)
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
                    textStyle = TextStyle(fontSize = 16.sp)
                )

                // Prezzo (codice esistente)
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
                    val reservation = parkingSpot.id?.let {
                        Reservation(
                            id = null,
                            user = SessionManager.user!!,
                            price = price.toDouble(),
                            paymentMethod = PaymentMethodType.PAYPAL,
                            parkingSpotId = it,
                            licensePlate = licensePlate.text,
                            startDate = searchCriteria.startDate,
                            endDate = searchCriteria.endDate,
                        )
                    }
                    if (reservation != null) {
                        currentReservation = reservation
                        showPaymentDialog = true
                    }
                },
                enabled = licensePlate.text.isNotEmpty() && licensePlate.text.length >= 6,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("Procedi al pagamento")
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

    // Dialog di pagamento PayPal
    currentReservation?.let { reservation ->
        if (showPaymentDialog) {
            PaymentDialog(
                isOpen = true,
                amount = price,
                reservation = reservation,
                onClose = {
                    showPaymentDialog = false
                },
                onSuccess = { completedReservation ->
                    onConfirm(completedReservation)
                    showPaymentDialog = false
                }
            )
        }
    }
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


@Composable
fun ConfirmationDialog(
    parkingSpot: ParkingSpot,
    parkingSpace: ParkingSpace,
    reservation: Reservation,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Prenotazione Confermata!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dettagli prenotazione:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Posto: #${parkingSpot.number}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Via: ${parkingSpace.address.street ?: "Indirizzo non disponibile"}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Inizio: ${reservation.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Fine: ${reservation.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Prezzo: €${String.format("%.2f", reservation.price)}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

fun calculatePrice(startDate: LocalDateTime, endDate: LocalDateTime, basePrice: Double): Double {
    val minutes = ChronoUnit.MINUTES.between(startDate, endDate)

    val finalPrice = basePrice + minutes * (0.01 * basePrice)
    return  finalPrice
}
