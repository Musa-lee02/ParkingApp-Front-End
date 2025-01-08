package com.example.parkingappfront_end.ui.reservation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
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
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import com.example.parkingappfront_end.viewmodels.ReservationViewModel
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.core.graphics.toRect
import com.example.parkingappfront_end.R
import com.example.parkingappfront_end.model.DateAxisValueFormatter
import com.example.parkingappfront_end.ui.home.DateSelector
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.parkingappfront_end.model.IndexAxisValueFormatter
import com.github.mikephil.charting.components.MarkerView

@Composable
fun MainStatisticView(
    navController: NavController,
    viewModel: ParkingViewModel,
    reservationViewModel: ReservationViewModel,
) {
    val sortedPS by viewModel.sortedPSBy.collectAsState()
    val selectedParkingSpace = remember { mutableStateOf<ParkingSpace?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    // Stati per le date di inizio e fine
    val startDate = remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    val endDate = remember { mutableStateOf(LocalDate.now()) }

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

            // Date Selectors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateSelector(
                    label = "Data Inizio",
                    selectedDate = startDate.value,
                    minDate =  LocalDateTime.now().minusMonths(6).toLocalDate(),
                    maxDate =  LocalDateTime.now().toLocalDate(),
                    onDateSelected = { date -> startDate.value = date },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                DateSelector(
                    label = "Data Fine",
                    selectedDate = endDate.value,
                    minDate =  LocalDateTime.now().minusMonths(6).toLocalDate(),
                    maxDate =  LocalDateTime.now().toLocalDate(),
                    onDateSelected = { date -> endDate.value = date },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            selectedParkingSpace.value?.let { parkingSpace ->
                val selectedPair = sortedPS.firstOrNull { it.first == parkingSpace }
                selectedPair?.let {
                    SpaceStatsDetails(
                        parkingSpace = parkingSpace,
                        sortCriteria = it.second,
                        startDate = startDate.value,
                        endDate = endDate.value,
                        reservationViewModel = reservationViewModel
                    )
                }
            }
        }
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
    startDate: LocalDate,
    endDate: LocalDate,
    reservationViewModel: ReservationViewModel
) {
    val stats by reservationViewModel.filteredStats.collectAsState()

    LaunchedEffect(startDate, endDate, parkingSpace) {
        reservationViewModel.filterStatsByDate(parkingSpace, startDate, endDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Abilita lo scroll verticale
            .padding(16.dp)
    ) {
        Text(
            text = parkingSpace.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Primo grafico
        if (stats.isNotEmpty()) {
            val labels = reservationViewModel.generateLabels(startDate, endDate)
            val statsList = remember(stats, labels) {
                labels.map { date ->
                    Pair(date, stats[date] ?: 0.0)
                }
            }

            LineChartView(
                stats = statsList,
                filterType = FilterType.MONTHLY
            )
        } else {
            Text(
                text = "Nessun dato disponibile",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Secondo grafico (per gli spot)
        val spotStats = parkingSpace.parkingSpots?.associate { it.number to it.reservations.size } // Implementa la logica nel ViewModel
        if (spotStats?.isNotEmpty() == true) {
            val spotLabels = spotStats?.keys?.toList()
            val spotValues = spotStats?.values?.toList()

            BarChartView(
                labels = spotLabels?.map { it.toString() } ?: emptyList(),
                values = spotValues?.map { it.toDouble() } ?: emptyList()
            )
        } else {
            Text(
                text = "Ancora nessun dato sugli spot",
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LineChartView(
    stats: List<Pair<LocalDate, Double>>,
    filterType: FilterType
) {
    val entries = stats.map { (date, value) ->
        Entry(date.toEpochDay().toFloat(), value.toFloat())
    }.sortedBy { it.x }

    val primaryColor = Color( 0xFF00FF00)//green 0xFF00FF00

    val dataSet = LineDataSet(entries, "Guadagni").apply {
        color = primaryColor.toArgb()
        valueTextColor = Color.DarkGray.toArgb()
        valueTextSize = 12f
        setDrawCircles(false)  // Rimuove i cerchi
        lineWidth = 3f
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.15f
        setDrawValues(true)
        // Mostra i valori solo se maggiori di zero
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value > 0) "€%.2f".format(value) else ""
            }
        }
        setDrawFilled(true)
        fillDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                primaryColor.copy(alpha = 0.4f).toArgb(),
                primaryColor.copy(alpha = 0.1f).toArgb(),
                Color.Transparent.toArgb()
            )
        )
    }

    val lineData = LineData(dataSet)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp),
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description.isEnabled = false
                legend.apply {
                    textSize = 12f
                    textColor = Color.DarkGray.toArgb()
                    form = Legend.LegendForm.LINE
                    formSize = 15f
                    xEntrySpace = 10f
                    formLineWidth = 3f
                }
                setDrawGridBackground(false)
                setDrawBorders(true)
                setBorderColor(Color.LightGray.toArgb())

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = DateAxisValueFormatter(stats.map { it.first }, filterType)
                    granularity = 1f
                    labelRotationAngle = -45f
                    setDrawGridLines(false)
                    textColor = Color.DarkGray.toArgb()
                    textSize = 11f
                    yOffset = 10f
                    labelCount = 6
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.LightGray.copy(alpha = 0.5f).toArgb()
                    textColor = Color.DarkGray.toArgb()
                    textSize = 11f
                    axisMinimum = 0f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "€%.0f".format(value)
                        }
                    }
                }

                axisRight.isEnabled = false
                animateY(1200, Easing.EaseInOutQuart)
            }
        },
        update = { chart ->
            chart.data = lineData
            chart.invalidate()
        }
    )
}

@Composable
fun BarChartView(
    labels: List<String>,
    values: List<Double>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Distribuzione prenotazioni per spot",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.DarkGray
        )

        val entries = values.mapIndexed { spot, numSelled ->
            BarEntry(spot.toFloat(), numSelled.toFloat())
        }

        val primaryColor = Color(0xFF6200EE)

        val dataSet = BarDataSet(entries, "Prenotazioni per Spot").apply {
            color = primaryColor.toArgb()
            valueTextColor = Color.DarkGray.toArgb()
            valueTextSize = 13f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            setColor(primaryColor.toArgb())
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.8f
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                BarChart(context).apply {
                    data = barData
                    description.isEnabled = false  // Disabilitiamo la description interna del grafico
                    legend.apply {
                        isEnabled = true
                        textSize = 12f
                        textColor = Color.DarkGray.toArgb()
                        form = Legend.LegendForm.SQUARE
                        formSize = 12f
                        xEntrySpace = 10f
                    }
                    setDrawGridBackground(false)
                    setDrawBorders(true)
                    setBorderColor(Color.LightGray.toArgb())

                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        textColor = Color.DarkGray.toArgb()
                        textSize = 11f
                        labelRotationAngle = -45f
                        setDrawGridLines(false)
                    }

                    axisLeft.apply {
                        axisMinimum = 0f
                        granularity = 1f
                        textColor = Color.DarkGray.toArgb()
                        textSize = 11f
                        setDrawGridLines(true)
                        gridColor = Color.LightGray.copy(alpha = 0.5f).toArgb()
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return value.toInt().toString()
                            }
                        }
                    }

                    axisRight.isEnabled = false
                    animateY(1200, Easing.EaseInOutQuart)
                    setPinchZoom(true)
                    setScaleEnabled(true)
                }
            },
            update = { chart ->
                chart.data = barData
                chart.invalidate()
            }
        )
    }
}

enum class FilterType {
    DAILY,
    MONTHLY
}