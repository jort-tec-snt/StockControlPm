package com.jort.stockcontrolpm.ui.screens.products

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
fun ProductDetailScreen(
    productId: Long,
    onEditClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Detalle de producto",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Consulta de informacion completa del producto.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Producto seleccionado",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "ID: $productId")
                Text(text = "Stock: pendiente de conectar")
                Text(text = "Precio: pendiente de conectar")
                Text(text = "Vencimiento: pendiente de conectar")
            }
        }

        Button(
            onClick = { onEditClick(productId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Editar producto")
        }
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
    }
}

