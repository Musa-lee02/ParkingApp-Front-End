package com.example.parkingappfront_end.ui.reservation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parkingappfront_end.ui.theme.ParkingAppFrontEndTheme


@Preview(showBackground = true)
@Composable
fun PreviewPrenotazioneScreen() {
    ParkingAppFrontEndTheme{
        ReservationScreen(rememberNavController())
    }
}

@Composable
fun ReservationScreen(navController: NavController) {
    // Stato per gestire la visualizzazione del messaggio di conferma
    var prenotazioneConfermata by remember { mutableStateOf(false) }

    // Stato per memorizzare le selezioni dell'utente
    var colonnaSelezionata by remember { mutableStateOf("") }
    var numeroSelezionato by remember { mutableStateOf(0) }
    var oreSelezionate by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sei pronto per prenotare il tuo parcheggio?",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Menu a tendina Scegli colonna
        Spinner(
            label = "Scegli colonna",
            selectedItem = colonnaSelezionata.ifEmpty { "Scegli colonna" },
            items = listOf("A", "B", "C", "D"),
            onItemSelected = { colonnaSelezionata = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu a tendina Seleziona numero
        Spinner(
            label = "Seleziona numero",
            selectedItem = numeroSelezionato.takeIf { it != 0 }?.toString() ?: "Seleziona numero",
            items = (1..10).map { it.toString() },
            onItemSelected = { numeroSelezionato = it.toInt() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menu a tendina Seleziona ore
        Spinner(
            label = "Seleziona ore",
            selectedItem = oreSelezionate.takeIf { it != 0 }?.toString() ?: "Seleziona ore",
            items = (1..24).map { it.toString() },
            onItemSelected = { oreSelezionate = it.toInt() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pulsante Conferma Prenotazione
        Button(onClick = {
            prenotazioneConfermata = true
        }) {
            Text("Conferma Prenotazione")
        }

        // Mostra il messaggio di conferma se prenotazioneConfermata è true
        if (prenotazioneConfermata) {
            Spacer(modifier = Modifier.height(16.dp))

            // Riquadro con sfondo blu per il recap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Prenotazione Confermata",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hai prenotato la colonna $colonnaSelezionata, numero $numeroSelezionato per $oreSelezionate ore.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pulsante per tornare alla home
            Button(
                onClick = {
                    navController.navigate("payment") {
                        // Navigazione con il popbackstack per rimuovere la schermata corrente dalla pila
                        popUpTo("reservation") { inclusive = true }
                    }
                }
            ) {
                Text("Procedi al Pagamento")
            }
        }
    }
}

@Composable
fun Spinner(
    label: String,
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown

    Column {
        // Mostra la selezione attuale sopra il menu
        Text(
            text = "$label: $selectedItem",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Mostra il label del menu (es. "Scegli colonna")
            Text(label)
            Icon(imageVector = icon, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp) // Imposta una altezza massima per il menu
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier
                        .height(40.dp), // Imposta l'altezza dell'item per ridurre la dimensione del menu
                    text = { Text(item, style = MaterialTheme.typography.bodySmall) } // Imposta una dimensione più piccola per il testo
                )
            }
        }
    }
}