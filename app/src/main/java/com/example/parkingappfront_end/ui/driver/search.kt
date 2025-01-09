package com.example.parkingappfront_end.ui.driver

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.ZoneId
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.time.LocalTime
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Shape


@Composable
fun ParkingSearchScreen(
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
    onSearch: (String, LocalDateTime, LocalDateTime) -> Unit
) {
    var city by remember { mutableStateOf("") }
    val romeZone = ZoneId.of("Europe/Rome")
    val nowInRome = LocalDateTime.now(romeZone)
    var startDate by remember { mutableStateOf(nowInRome.plusMinutes(5)) }
    var endDate by remember { mutableStateOf(startDate.plusHours(1)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Trova il tuo parcheggio",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Città") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "City Icon"
                )
            },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Riga: Data e ora di inizio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateSelector(
                label = "Data di inizio",
                selectedDate = startDate.toLocalDate(),
                minDate = nowInRome.toLocalDate(),
                maxDate = endDate.plusWeeks(8).toLocalDate(),
                onDateSelected = { date ->
                    startDate = LocalDateTime.of(date, startDate.toLocalTime())
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            TimeSelector(
                label = "Ora di inizio",
                selectedTime = startDate.toLocalTime(),
                onTimeSelected = { time ->
                    startDate = LocalDateTime.of(startDate.toLocalDate(), time)
                },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Riga: Data e ora di fine
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateSelector(
                label = "Data di fine",
                selectedDate = endDate.toLocalDate(),
                minDate = nowInRome.toLocalDate(),
                maxDate = endDate.plusWeeks(8).toLocalDate(),
                onDateSelected = { date ->
                    endDate = LocalDateTime.of(date, endDate.toLocalTime())
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            TimeSelector(
                label = "Ora di fine",
                selectedTime = endDate.toLocalTime(),
                onTimeSelected = { time ->
                    endDate = LocalDateTime.of(endDate.toLocalDate(), time)
                },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pulsante di ricerca
        Button(
            onClick = {
                if (city.isNotEmpty() && startDate.isBefore(endDate) && startDate.isAfter(nowInRome)
                    && endDate.isAfter(nowInRome)
                    ) {
                    onSearch(city, startDate, endDate)
                } else {
                    errorMessage = "Completa tutti i campi correttamente, la data di inizio deve essere antecedente a quella di fine e nel futuro"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            contentPadding = PaddingValues(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Cerca Parcheggio")
        }

        // Messaggio di errore
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun DateSelector(
    label:String,
    selectedDate: LocalDate,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = MaterialTheme.colorScheme.outline
) {
    var showDialog by remember { mutableStateOf(false) }

    androidx.compose.material3.Surface(
        modifier = modifier.clickable { showDialog = true },
        shape = shape,
        tonalElevation = 2.dp,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if(showDialog) {
                datePickerDialog(
                    context = LocalContext.current,
                    selectedDate = selectedDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    onDateSelected = {
                        onDateSelected(it)
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}




fun datePickerDialog(
    context: Context,
    selectedDate: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Imposta la data minima alla data odierna
    datePickerDialog.datePicker.minDate = minDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
        ?.toEpochMilli() ?: System.currentTimeMillis()

    datePickerDialog.datePicker.maxDate = maxDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
        ?.toEpochMilli() ?: Long.MAX_VALUE

    datePickerDialog.apply {
        setOnDismissListener { onDismiss() }
        show()
    }
}



@Composable
fun TimeSelector(
    label: String,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape
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
























