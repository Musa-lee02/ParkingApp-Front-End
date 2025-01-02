package com.example.parkingappfront_end.ui.checkout

import CheckoutViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingappfront_end.SessionManager.user
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch


@Composable
fun CheckoutScreen(viewModel: CheckoutViewModel, navController: NavController) {

        //val isBuyNowEnabled by viewModel.isCheckoutEnabled.collectAsState()
        //val selectedAddress = viewModel.selectedAddress.collectAsState().value
        val selectedPaymentMethod = viewModel.selectedPaymentMethod.collectAsState().value

        LaunchedEffect(Unit) {
            //viewModel.loadCheckoutData()
        }


            Column(modifier = Modifier.padding(16.dp)) {



            // Sezione Indirizzo di Spedizione (con freccia per modificare)

            Spacer(modifier = Modifier.height(16.dp))

            // Sezione Metodo di Pagamento (con freccia per modificare)
            CheckoutSectionWithArrow(
                title = "Payment Method",
                content = selectedPaymentMethod?.let {
                    "${it.cardHolderName} - ${it.cardNumber}"
                } ?: "Select a payment method",
                onClick = { navController.navigate("checkout-payment") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Riepilogo Totale
            CheckoutTotalSection(
                totalAmount = viewModel.totalAmount.collectAsState().value
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pulsante per confermare l'ordine
            Button(
                onClick = {
                    viewModel.isLoading
                },


            ) {
                Text(
                    text = "Buy Now",
                    color = Color.White) // Colore del testo
            }
        }
    }

    @Composable
    fun CheckoutSectionWithArrow(title: String, content: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = content, style = MaterialTheme.typography.bodySmall)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Edit",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    @Composable
    fun CheckoutTotalSection(totalAmount: Double) {
        Column {
            Text("Order Summary", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.bodyMedium)
                Text("€$totalAmount", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }


