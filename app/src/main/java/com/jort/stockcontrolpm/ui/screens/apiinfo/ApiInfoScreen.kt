package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApiInfoScreen(
    onDashboardClick: () -> Unit,
    onProductsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "API Info",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Pantalla reservada para el consumo de un endpoint publico con Retrofit.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Estado remoto",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Loading: pendiente")
                Text(text = "Success: pendiente")
                Text(text = "Error: pendiente")
            }
        }

        Button(
            onClick = onDashboardClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver al dashboard")
        }
        OutlinedButton(
            onClick = onProductsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver productos")
        }
    }
}

