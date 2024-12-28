package com.example.parkingappfront_end.ui.home

import android.content.Context
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.parkingappfront_end.SessionManager
import com.example.parkingappfront_end.ui.reservation.ParkingSpaceDetails
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import kotlin.math.pow
import kotlin.math.sqrt

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ParkingSearchScreen(
    onSearch: (String, LocalDateTime, LocalDateTime) -> Unit,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {
    var city by remember { mutableStateOf("") }
    val romeZone = ZoneId.of("Europe/Rome")
    val nowInRome = LocalDateTime.now(romeZone)
    var startDate by remember { mutableStateOf(nowInRome.plusMinutes(5)) }
    var endDate by remember { mutableStateOf(startDate.plusHours(1)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trova il tuo parcheggio",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Città") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "City Icon"
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Prima riga: Data di inizio e ora di inizio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateSelector(
                label = "Data di inizio",
                selectedDate = startDate.toLocalDate(),
                onDateSelected = { date ->
                    startDate = LocalDateTime.of(date, startDate.toLocalTime())
                },
                modifier = Modifier.weight(1f)
            )

            TimeSelector(
                label = "Ora di inizio",
                selectedTime = startDate.toLocalTime(),
                onTimeSelected = { time ->
                    startDate = LocalDateTime.of(startDate.toLocalDate(), time)
                },
                modifier = Modifier.weight(1f)
            )
        }

        // Seconda riga: Data di fine e ora di fine
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateSelector(
                label = "Data di fine",
                selectedDate = endDate.toLocalDate(),
                onDateSelected = { date ->
                    if (date.isAfter(startDate.toLocalDate())) {
                        endDate = LocalDateTime.of(date, endDate.toLocalTime())
                        errorMessage = null
                    } else {
                        errorMessage = "La data di fine deve essere successiva alla data di inizio"
                    }
                },
                modifier = Modifier.weight(1f)
            )

            TimeSelector(
                label = "Ora di fine",
                selectedTime = endDate.toLocalTime(),
                onTimeSelected = { time ->
                    if (LocalDateTime.of(endDate.toLocalDate(), time).isAfter(startDate)) {
                        endDate = LocalDateTime.of(endDate.toLocalDate(), time)
                        errorMessage = null
                    } else {
                        errorMessage = "L'ora di fine deve essere successiva all'ora di inizio"
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (city.isNotEmpty() && startDate.isBefore(endDate)) {
                    onSearch(city, startDate, endDate)
                } else {
                    errorMessage = "Completa tutti i campi correttamente"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Cerca Parcheggio")
        }
    }
}

@Composable
fun DateSelector(
    label: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = {},
        label = { Text(label) },
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Date Icon")
            }
        }
    )
    if (showDialog) {
        DatePickerDialog(
            context = context,
            selectedDate = selectedDate,
            onDateSelected = {
                onDateSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun TimeSelector(
    label: String,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        label = { Text(label) },
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.AccessTime, contentDescription = "Time Icon")
            }
        }
    )
    if (showDialog) {
        TimePickerDialog(
            context = context,
            selectedTime = selectedTime,
            onTimeSelected = {
                onTimeSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

fun DatePickerDialog(
    context: Context,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
    }
    android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            onDateSelected(LocalDate.of(year, month + 1, day))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnDismissListener { onDismiss() }
        show()
    }
}

fun TimePickerDialog(
    context: Context,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, selectedTime.hour)
        set(Calendar.MINUTE, selectedTime.minute)
    }
    android.app.TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected(LocalTime.of(hour, minute))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).apply {
        setOnDismissListener { onDismiss() }
        show()
    }
}




fun calculateDistance(x1 : Double,y1 : Double, x2 : Double, y2 : Double) : Double {
    return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

}

/* (41.9028, 12.4964):

(41.92057, 12.50625)
(41.90645, 12.49826)
(41.92417, 12.25571)
(41.85089, 12.41823)
(41.90066, 12.49615)
(42.02622, 12.66282)
(41.81111, 12.77006)
(41.87868, 12.65586)
(41.73607, 12.61058)
(41.81317, 12.51199) */
























