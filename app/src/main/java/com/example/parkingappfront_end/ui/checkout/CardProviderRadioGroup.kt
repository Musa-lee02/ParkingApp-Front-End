package com.example.parkingappfront_end.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.parkingappfront_end.model.CardProvider


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun CardProviderRadioGroup(
    selectedProvider: CardProvider?,
    onProviderSelected: (CardProvider) -> Unit
) {
    Column {
        CardProvider.entries.forEach { provider ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProviderSelected(provider) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = provider == selectedProvider,
                    onClick = { onProviderSelected(provider) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = provider.displayName)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



