package com.example.parkingappfront_end.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/*
@Composable
fun OrderConfirmationScreen( navController: NavController, checkoutViewModel: CheckoutViewModel) {
    val order = checkoutViewModel.order.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Order Confirmed!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            if (order != null) {
                Text("Your order ID is: ${order.id}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("home") { // Naviga verso Home
                    popUpTo("checkout") { inclusive = true }  // Rimuovi la schermata di checkout dallo stack di navigazione
                }
            }) {
                Text("Go to Home")
            }
        }
    }
}
*/