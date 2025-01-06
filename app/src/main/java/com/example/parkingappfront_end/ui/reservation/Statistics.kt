package com.example.parkingappfront_end.ui.reservation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.model.SpacesSortCriterias
import com.example.parkingappfront_end.model.Statistic
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.util.Locale
import androidx.compose.runtime.Composable
import com.example.parkingappfront_end.R
import com.example.parkingappfront_end.model.CustomMarkerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar

@Composable
fun MainStatisticView(
    navController: NavController,
    viewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
) {
    val sortedPS by viewModel.sortedPSBy.collectAsState()
    val selectedParkingSpace = remember { mutableStateOf<ParkingSpace?>(null) }
    val selectedFilter = remember { mutableStateOf(FilterType.WEEKLY) } // Gestione filtro
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(sortedPS) {
        if (sortedPS.isNotEmpty()) {
            selectedParkingSpace.value = selectedParkingSpace.value ?: sortedPS.first().first
        } else {
            viewModel.loadParkingSpacesByOwner()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            SpaceList(
                selectedParkingSpace = selectedParkingSpace.value,
                onParkingSpaceSelected = { parkingSpace ->
                    selectedParkingSpace.value = parkingSpace
                },
                viewModel = viewModel
            )

            FilterButtons(
                selectedFilter = selectedFilter.value,
                onFilterSelected = { filter ->
                    selectedFilter.value = filter
                }
            )

            selectedParkingSpace.value?.let { parkingSpace ->
                val selectedPair = sortedPS.firstOrNull { it.first == parkingSpace }
                selectedPair?.let {
                    SpaceStatsDetails(
                        parkingSpace = parkingSpace,
                        sortCriteria = it.second,
                        filterType = selectedFilter.value,
                        parkingViewModel = viewModel,
                        reservationViewModel = reservationViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun FilterButtons(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        FilterButton(
            label = "Oraria",
            isSelected = selectedFilter == FilterType.TIMELY,
            onClick = { onFilterSelected(FilterType.TIMELY) }
        )
        FilterButton(
            label = "Settimanale",
            isSelected = selectedFilter == FilterType.WEEKLY,
            onClick = { onFilterSelected(FilterType.WEEKLY) }
        )
        FilterButton(
            label = "Mensile",
            isSelected = selectedFilter == FilterType.MONTHLY,
            onClick = { onFilterSelected(FilterType.MONTHLY) }
        )
    }
}

@Composable
fun FilterButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Blue else MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text = label, color = if (isSelected) Color.White else Color.Black)
    }
}


@Composable
fun SpaceList(
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
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Resoconto parcheggi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                showAddParkingSpace = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter"
            )
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
fun SpaceStatsDetails(
    parkingSpace: ParkingSpace,
    sortCriteria: SpacesSortCriterias,
    filterType: FilterType,
    parkingViewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel
) {
    val isSortedByDistance by parkingViewModel.isSortedByDistance.collectAsState()
    val timelyStats by reservationViewModel.timelyStats.collectAsState()
    val weeklyStats by reservationViewModel.weeklyStats.collectAsState()
    val monthlyStats by reservationViewModel.monthlyStats.collectAsState()

    LaunchedEffect(filterType, parkingSpace) {
        when (filterType) {
            FilterType.TIMELY -> reservationViewModel.calculateSalesTimely(parkingSpace)
            FilterType.WEEKLY -> reservationViewModel.calculateSalesWeekly(parkingSpace)
            FilterType.MONTHLY -> reservationViewModel.calculateSalesMonthly(parkingSpace)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
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

        Spacer(modifier = Modifier.height(10.dp))

        val currentStats = when (filterType) {
            FilterType.TIMELY -> timelyStats
            FilterType.WEEKLY -> weeklyStats
            FilterType.MONTHLY -> monthlyStats
        }

        if (currentStats.isNotEmpty()) {
            LineChartView(
                stats = currentStats,
                filterType = filterType
            )
        } else {
            Text(
                text = "Nessun dato disponibile",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LineChartView(stats: HashMap<String, Double>, filterType: FilterType) {
    val context = LocalContext.current

    // Funzione per ottenere tutte le possibili fasce temporali
    fun getAllTimeSlots(): List<String> = when (filterType) {
        FilterType.TIMELY -> (0..23).map { hour ->
            if (hour < 10) "0$hour:00" else "$hour:00"
        }
        FilterType.WEEKLY -> listOf("Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica")
        FilterType.MONTHLY -> listOf("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre")
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { ctx ->
            LineChart(ctx).apply {
                description.isEnabled = false
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    labelRotationAngle = -45f
                    granularity = 1f
                    setDrawGridLines(true)
                    textSize = 10f
                }
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                    textSize = 12f
                    setDrawLabels(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "€${value.toInt()}"
                        }
                    }
                }
                axisRight.isEnabled = false
                legend.apply {
                    isEnabled = true
                    textSize = 12f
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                }
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                marker = CustomMarkerView(context, R.layout.marker_view)
            }
        },
        update = { chart ->
            val timeSlots = getAllTimeSlots()

            val entries = timeSlots.mapIndexed { index, timeSlot ->
                Entry(index.toFloat(), stats[timeSlot]?.toFloat() ?: 0f)
            }

            val timeLabel = when (filterType) {
                FilterType.TIMELY -> "Vendite orarie"
                FilterType.WEEKLY -> "Vendite settimanali"
                FilterType.MONTHLY -> "Vendite mensili"
            }

            val dataSet = LineDataSet(entries, timeLabel).apply {
                color = Color.Green.toArgb()
                setCircleColor(Color.Blue.toArgb())
                circleRadius = 5f
                lineWidth = 2f
                valueTextSize = 10f
                valueTextColor = Color.Black.toArgb()
                mode = LineDataSet.Mode.LINEAR
                setDrawValues(true)
                setDrawFilled(true)
                fillColor = Color.Green.toArgb()  // Modificato come richiesto
                fillAlpha = 50
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "€${value.toInt()}"
                    }
                }
            }

            val lineData = LineData(dataSet)
            chart.data = lineData

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(timeSlots)

            val maxSales = stats.values.maxOrNull()?.toFloat() ?: 0f
            chart.axisLeft.apply {
                axisMaximum = maxSales + (maxSales * 0.1f)
                labelCount = 5
            }

            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
// Funzione helper per convertire il numero del mese nel nome
private fun getMonthName(monthNumber: String): String {
    return try {
        val month = monthNumber.toInt()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        monthFormat.format(calendar.time)
    } catch (e: Exception) {
        monthNumber
    }
}



enum class FilterType {
   TIMELY, WEEKLY, MONTHLY
}