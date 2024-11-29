package com.example.parkingappfront_end.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(navController: NavController) {
    var vehiclePlate by remember { mutableStateOf("") }  // Stato per gestire l'input della targa

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titolo
        Text(
            text = "BENVENUTO SU PARKING APP",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Immagine in alto
        Image(
            painter = painterResource(id = R.drawable.parcheggio),
            contentDescription = "Mappa del parcheggio",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)  // Altezza dell'immagine (regolabile)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Inserisci la targa del veicolo e prenota il tuo parcheggio!",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))



        // Campo di input per la targa del veicolo
        TextField(
            value = vehiclePlate,
            onValueChange = { vehiclePlate = it },
            label = { Text("Inserisci la targa del veicolo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Pulsante "Prenota"
        Button(
            onClick = {
                println("Targa inserita: $vehiclePlate")  // Log per il debug
                navController.navigate("reservation")
            },
            enabled = vehiclePlate.isNotBlank()  // Abilita il pulsante solo se la targa è compilata
        ) {
            Text("Prenota")
        }
    }
}
