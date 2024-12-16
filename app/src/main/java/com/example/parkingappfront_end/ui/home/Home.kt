package com.example.parkingappfront_end.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingappfront_end.R
import com.example.parkingappfront_end.model.ParkingSpace
import com.example.parkingappfront_end.viewmodels.ParkingViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.parkingappfront_end.model.ParkingSpot
import com.example.parkingappfront_end.ui.reservation.ParkingSpaceDetails


@Composable
fun HomeScreen(navController: NavController, viewModel: ParkingViewModel) {
    val parkingSpaces by viewModel.parkingSpaces.collectAsState()
    val parkingSpots by viewModel.parkingSpots.collectAsState()

    var licensePlate by remember { mutableStateOf("") }

    val selectedParkingSpace = remember(parkingSpaces) {
        mutableStateOf(parkingSpaces.firstOrNull() ?: ParkingSpace.default())
    }



    LaunchedEffect(Unit) {
        viewModel.loadParkingSpaces()
        Log.d("Parking", "Home screen Parking spaces loaded: ${parkingSpaces}")
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
    )
    {
        item {
            ParkingSpaceList(
                parkingSpaces = parkingSpaces,
                viewModel = viewModel,
                selectedParkingSpace = selectedParkingSpace.value,
                onParkingSpaceSelected = { parkingSpace ->
                    selectedParkingSpace.value = parkingSpace.copy()
                    parkingSpace.id.let { viewModel.loadParkingSpots(parkingSpace.id) }
                }
            )
        }
        selectedParkingSpace.value?.let { parkingSpace ->
            if (parkingSpace != null) {
                item {
                    ParkingSpaceDetails(pSpots = parkingSpots, parkingSpace = parkingSpace, viewModel = viewModel)
                }
            }
        }


    }
        
}


@Composable
fun ParkingSpaceList(parkingSpaces: List<ParkingSpace>, selectedParkingSpace: ParkingSpace?, viewModel: ParkingViewModel,onParkingSpaceSelected: (ParkingSpace) -> Unit) {
    /*
    var showAddWishlistMain by remember { mutableStateOf(false) }
    val isAdmin = user?.role == "ROLE_ADMIN"
    val idUserSelectedByAdmin by viewModel.userSelectedByAdmin.collectAsState()*/

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectedParkingSpace?.name?.let {
            Text(
                text = it,

                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


    }

    if (parkingSpaces.isEmpty()) { // Controlla se la lista è vuota
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
    } else{
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(
                items =parkingSpaces,
                //key = { item -> "${item.id}-${item.name}" }// Chiave a livello di items
            ) { parkingSpace ->
                var isFriendWishlist: Boolean? = null
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
            // Aggiungi eventuali altri dettagli della wishlist qui
        }
    }

}