package com.example.parkingappfront_end.ui.home

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


@Composable
fun HomeScreen(navController: NavController, parkingViewModel: ParkingViewModel, reservationViewModel: ReservationViewModel) {
    val parkingSpacesWithDistances by parkingViewModel.sortedPSBy.collectAsState()
    val parkingSpots by parkingViewModel.parkingSpots.collectAsState()
    val userPosition = SessionManager.position

    var licensePlate by remember { mutableStateOf("") }

    val selectedParkingSpace = remember(parkingSpacesWithDistances) {
        mutableStateOf(parkingSpacesWithDistances.firstOrNull()?.first ?: ParkingSpace.default())
    }

    var isLoading by remember { mutableStateOf(true) }
    var loadingParkingSpaces by remember { mutableStateOf(true) }

    LaunchedEffect(parkingViewModel) {
        loadingParkingSpaces = true
        try {
            parkingViewModel.loadParkingSpaces()
            loadingParkingSpaces = false
        } catch (e: Exception) {
            loadingParkingSpaces = false
        }
    }

    LaunchedEffect(parkingSpacesWithDistances) {
        if (parkingSpacesWithDistances.isNotEmpty()) {
            val firstSpace = parkingSpacesWithDistances.firstOrNull()?.first ?: ParkingSpace.default()
            selectedParkingSpace.value = firstSpace
            firstSpace.id?.let { parkingViewModel.loadParkingSpots(it) }
        }
    }

    LaunchedEffect(userPosition) {
        userPosition?.let {
            parkingViewModel.sortPSByDistance(it.latitude, it.longitude)
        }
    }

    LaunchedEffect(loadingParkingSpaces, userPosition) {
        if (!loadingParkingSpaces && userPosition != null) {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier =Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ParkingSpaceList(
                    parkingSpaces = parkingSpacesWithDistances.map { it.first },
                    selectedParkingSpace = selectedParkingSpace.value,
                    onParkingSpaceSelected = { parkingSpace ->
                        selectedParkingSpace.value = parkingSpace.copy()
                        parkingSpace.id?.let { parkingViewModel.loadParkingSpots(it) }
                    },
                    viewModel = parkingViewModel
                )
            }
            selectedParkingSpace.value?.let { parkingSpace ->
                item {
                    val selectedPair = parkingSpacesWithDistances.firstOrNull { it.first == parkingSpace }
                    selectedPair?.let {
                        ParkingSpaceDetails(
                            parkingSpace = it.first,
                            sortCriteria = it.second,
                            pSpots = parkingSpots,
                            parkingViewModel = parkingViewModel,
                            reservationViewModel = reservationViewModel,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ParkingSpaceList(
    parkingSpaces: List<ParkingSpace>,
    selectedParkingSpace: ParkingSpace?,
    viewModel: ParkingViewModel,
    onParkingSpaceSelected: (ParkingSpace) -> Unit
) {

    val sortByDistance by viewModel.sortedByDistance.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lista Parcheggi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                if (sortByDistance) {
                    viewModel.sortPSByPrice()
                } else {
                    viewModel.sortPSByDistance(SessionManager.position!!.latitude, SessionManager.position!!.longitude)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = if (sortByDistance) "Ordina per prezzo" else "Ordina per distanza"
            )
        }
    }

    if (parkingSpaces.isEmpty()){
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
                items = parkingSpaces,
            ) { parkingSpace ->
                ParkingSpaceThumbnail(
                    parkingSpace = parkingSpace,
                    onClick = {
                        onParkingSpaceSelected(parkingSpace)
                    },
                    isSelected = selectedParkingSpace == parkingSpace
                )
            }
        }
    }
}

@Composable
fun ParkingSpaceThumbnail(parkingSpace : ParkingSpace, onClick: () -> Unit, isSelected: Boolean) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(70.dp) // Imposta l'altezza fissa per tutte le card
            .clickable { onClick() },

        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE0E0E0) else Color.White, // Colore di sfondo condizionale
        ), // Colore di riempimento condizionale

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = parkingSpace.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f),
                textAlign = TextAlign.Center
                //.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.height(4.dp))

        }
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
























