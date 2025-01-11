package com.example.parkingappfront_end.ui.driver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import com.example.parkingappfront_end.model.Reservation
import com.example.parkingappfront_end.model.ReservationWithDetails
import com.example.parkingappfront_end.model.domain.SpotType
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainReservations(reservationViewModel: ReservationViewModel, navController: NavHostController) {
    val reservations by reservationViewModel.myReservations.collectAsState()
    val selectedReservation = remember { mutableStateOf<ReservationWithDetails?>(null) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Le mie prenotazioni",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(reservations) { reservation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .animateItemPlacement(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            //horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when(reservation.spotType) {
                                    SpotType.CAR -> Icons.Filled.DirectionsCar
                                    SpotType.MOTORCYCLE -> Icons.Filled.Motorcycle
                                    null -> Icons.Filled.Motorcycle
                                },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Targa: ${reservation.licensePlate}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "€${reservation.price}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            //horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reservation.spaceName ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary

                            )
                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "Posto: ${reservation.spotNumber ?: "Non disponibile"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        ReservationDateTimeInfo(
                            startDate = reservation.startDate,
                            endDate = reservation.endDate
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            //horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Filled.Payment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = reservation.paymentMethod.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.weight(1f))

                        }

                        //Spacer(modifier = Modifier.height(8.dp))

                        if (isReservationCancellable(reservation.startDate)) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = {
                                        selectedReservation.value = reservation
                                        showDeleteConfirmationDialog = true
                                    },
                                    modifier = Modifier.width(200.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Annulla Prenotazione")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = {
                Text(
                    "Conferma cancellazione",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text("Sei sicuro di voler cancellare questa prenotazione? " +
                        "\n Verrà rimborsato l'importo pagato sul metodo di pagamento utilizzato.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedReservation.value?.id?.let {
                            reservationViewModel.deleteReservation(it)
                        }
                        showDeleteConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmationDialog = false }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
private fun ReservationDateTimeInfo(startDate: LocalDateTime, endDate: LocalDateTime) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Data inizio",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = startDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = startDate.format(timeFormatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Data fine",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = endDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = endDate.format(timeFormatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun isReservationCancellable(startDate : LocalDateTime): Boolean {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val now = LocalDateTime.now()
        startDate.isAfter(now)
    } catch (e: Exception) {
        false
    }
}