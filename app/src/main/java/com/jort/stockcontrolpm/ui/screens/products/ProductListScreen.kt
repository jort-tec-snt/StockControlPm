package com.jort.stockcontrolpm.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductListScreen(
    onCreateProductClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Productos",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Listado local de productos del minimarket.",
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(text = "Buscar producto") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        EmptyProductListCard(
            onProductClick = { onProductClick(1L) }
        )

        Button(
            onClick = onCreateProductClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Nuevo producto")
        }
    }
}

@Composable
private fun EmptyProductListCard(
    onProductClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sin productos registrados",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "En el siguiente bloque se conectara esta pantalla con datos locales.",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onProductClick) {
                    Text(text = "Detalle ejemplo")
                }
            }
        }
    }
}

