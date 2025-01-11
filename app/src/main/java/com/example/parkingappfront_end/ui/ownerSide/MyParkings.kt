package com.example.parkingappfront_end.ui.ownerSide

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingappfront_end.model.domain.SpotType


@Composable
fun AddParkingSpaceDialog(
    onDismissRequest: () -> Unit,
    onAddSpace: (String, String, String) -> Unit,
) {

    var showDialog by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }

    var nameValid by remember { mutableStateOf(false) }
    var cityValid by remember { mutableStateOf(false) }
    var streetValid by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = "Crea una nuova area di parcheggio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Campo per il nome della wishlist
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameValid = it.isNotBlank()

                            },
                            label = { Text("Nome") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = city,
                            onValueChange = {
                                city = it
                               cityValid = it.isNotBlank()

                            },
                            label = { Text("Citta'") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = street,
                            onValueChange = {
                                street = it
                                streetValid = it.isNotBlank()

                            },
                            label = { Text("Via") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = {
                            onAddSpace(name, city, street)
                            onDismissRequest()
                        },
                        enabled = nameValid && cityValid && streetValid // Abilita il pulsante solo se il campo non è vuoto
                    ) {
                        Text("Crea")
                    }
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    )
                    {
                        Text("Cancel")

                    }
                }
            },
            dismissButton = null,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .heightIn(max = 450.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingSpotDialog(
    onDismissRequest: () -> Unit,
    onAddSpot: (String, Double, SpotType) -> Unit,
) {
    var number by remember { mutableStateOf("") }
    var basePrice by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(SpotType.CAR) } // Default value

    val isNumberValid = number.isNotBlank()
    val isBasePriceValid = basePrice.toDoubleOrNull() != null

    val types = SpotType.values()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Aggiungi un posto auto",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo per il numero del posto
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Numero posto") },
                    isError = !isNumberValid && number.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                if (!isNumberValid && number.isNotEmpty()) {
                    Text(
                        text = "Il numero del posto non può essere vuoto",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // Campo per il prezzo base
                OutlinedTextField(
                    value = basePrice,
                    onValueChange = { basePrice = it },
                    label = { Text("Prezzo base (€)") },
                    isError = !isBasePriceValid && basePrice.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (!isBasePriceValid && basePrice.isNotEmpty()) {
                    Text(
                        text = "Inserisci un valore numerico valido",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                // Campo a tendina per il tipo di posto
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedType.name,
                        onValueChange = {},
                        label = { Text("Tipo di posto") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isNumberValid && isBasePriceValid) {
                        onAddSpot(number, basePrice.toDouble(), selectedType)
                        onDismissRequest()
                    }
                },
                enabled = isNumberValid && isBasePriceValid
            ) {
                Text("Aggiungi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Annulla")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    )
}
