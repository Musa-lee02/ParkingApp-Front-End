package com.example.parkingappfront_end.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.parkingappfront_end.model.CardProvider
import com.example.parkingappfront_end.model.PaymentMethodType
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun isExpirationDateValid(expirationDate: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("MM/yy")
        val expirationYearMonth = YearMonth.parse(expirationDate, formatter)
        val currentYearMonth = YearMonth.now()

        expirationYearMonth.isAfter(currentYearMonth) // Verifica se la data di scadenza è maggiore della data corrente
    } catch (e: DateTimeParseException) {
        false // Restituisce false se la data non è nel formato corretto
    }
}


@Composable
fun AddPaymentMethodForm(
    isExpanded: Boolean,
    onExpandChange: () -> Unit,
    cardHolderName: String,
    cardNumber: String,
    expirationDate: String,
    selectedCardProvider: CardProvider?,
    selectedPaymentMethodType: PaymentMethodType?,
    onCardHolderNameChange: (String) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onExpirationDateChange: (String) -> Unit,
    onCardProviderSelected:   (CardProvider?) -> Unit,
    onPaymentMethodTypeSelected: (PaymentMethodType) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // RIGA CHE MOSTRA L'INTESTAZIONE DEL FORM E LA FRECCIA PER ESPANDERE/COMPRIMERE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandChange() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Add New Payment Method", style = MaterialTheme.typography.headlineSmall)
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }

        // FORM PER L'AGGIUNTA DI UN NUOVO METODO DI PAGAMENTO
        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Payment Method Type", style = MaterialTheme.typography.titleSmall)
            PaymentMethodTypeRadioGroup(
                selectedPaymentMethodType = selectedPaymentMethodType,
                onPaymentMethodTypeSelected = onPaymentMethodTypeSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo per il titolare della carta
            OutlinedTextField(
                value = cardHolderName,
                onValueChange = onCardHolderNameChange,
                label = { Text("Card Holder Name") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo per il numero della carta
            var formattedCardNumber by remember { mutableStateOf(cardNumber) }

            var cardNumberField by remember { mutableStateOf(TextFieldValue("")) }

            OutlinedTextField(
                value = cardNumberField,
                onValueChange = { newValue ->
                    val digitsOnly = newValue.text.filter { it.isDigit() }.take(16) // Filtra solo i numeri e prendi i primi 16
                    val formatted = digitsOnly.chunked(4).joinToString(" ")

                    // Calcola il nuovo offset del cursore
                    val newCursorPosition = when {
                        newValue.text.length > cardNumberField.text.length -> formatted.length
                        else -> minOf(formatted.length, newValue.selection.start)
                    }

                    cardNumberField = TextFieldValue(
                        text = formatted,
                        selection = TextRange(newCursorPosition)  // Imposta la nuova posizione del cursore
                    )

                    onCardNumberChange(digitsOnly.toString()) // Aggiorna il ViewModel con le sole cifre
                },
                label = { Text("Card Number") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Card Provider", style = MaterialTheme.typography.titleSmall)
            CardProviderRadioGroup(
                selectedProvider = selectedCardProvider,
                onProviderSelected = onCardProviderSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo per la data di scadenza
            OutlinedTextField(
                value = expirationDate,
                onValueChange = onExpirationDateChange,
                label = { Text("Expiration Date (MM/YY)") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            val expirationDateRegex = "^(0[1-9]|1[0-2])\\/([0-9]{2})$".toRegex()

            var savable = cardHolderName.isNotBlank() && cardNumber.isNotBlank()
                    && cardNumber.length == 16
                    && expirationDate.isNotBlank() && expirationDate.matches(expirationDateRegex)
                    && isExpirationDateValid(expirationDate)
                    && selectedCardProvider != null && selectedPaymentMethodType != null

            //1234567891234567
            // Pulsante per aggiungere il nuovo metodo di pagamento
            Button(onClick = onSaveClick, modifier = Modifier.fillMaxWidth(), enabled = savable) {
                Text("Add Payment Method")
            }
        }
    }
}

@Composable
fun PaymentMethodTypeRadioGroup(
    selectedPaymentMethodType: PaymentMethodType?,
    onPaymentMethodTypeSelected: (PaymentMethodType) -> Unit
) {
    Column {
        PaymentMethodType.entries.forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPaymentMethodTypeSelected(type) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = type == selectedPaymentMethodType,
                    onClick = { onPaymentMethodTypeSelected(type) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = type.displayName)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
