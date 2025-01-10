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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


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

@Composable
fun AddParkingSpotDialog(
    onDismissRequest: () -> Unit,
    onAddSpot: (String, Double) -> Unit,
) {

    var showDialog by remember { mutableStateOf(true) }

    var number by remember { mutableStateOf("") }
    var basePrice by remember { mutableStateOf("") }

    var numberValid by remember { mutableStateOf(false) }
    var basePriceValid by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = "Aggiungi un posto auto",
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
                            value = number,
                            onValueChange = {
                                number = it
                                numberValid = it.isNotBlank()
                            },
                            label = { Text("Numero posto") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = basePrice,
                            onValueChange = {
                                    newText ->
                                if (newText.all { it.isDigit() }) {
                                    basePrice = newText
                                    basePriceValid = newText.isNotBlank()
                                }
                            },
                            label = { Text("Prezzo base") },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

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
                            onAddSpot(number, basePrice.toDouble())
                            onDismissRequest()
                        },
                        enabled = numberValid && basePriceValid // Abilita il pulsante solo se il campo non è vuoto
                    ) {
                        Text("Aggiungi")
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
                .heightIn(max = 330.dp)
        )
    }
}