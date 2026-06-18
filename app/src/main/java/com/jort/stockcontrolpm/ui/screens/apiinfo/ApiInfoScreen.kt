package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApiInfoScreen(
    uiState: ApiInfoUiState,
    onRetryClick: () -> Unit,
    onClearError: () -> Unit,
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

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
                Text(text = "Consultando productos externos...")
            }
            uiState.errorMessage != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error de conexion",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = uiState.errorMessage)
                        OutlinedButton(onClick = onClearError) {
                            Text(text = "Entendido")
                        }
                    }
                }
            }
            uiState.hasProducts -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { product -> product.id }
                    ) { product ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = product.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(text = "Categoria: ${product.category}")
                                Text(text = "Precio externo: $${product.price}")
                            }
                        }
                    }
                }
            }
            else -> {
                Text(text = "No hay datos remotos para mostrar.")
            }
        }

        OutlinedButton(
            onClick = onRetryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Reintentar consulta")
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
