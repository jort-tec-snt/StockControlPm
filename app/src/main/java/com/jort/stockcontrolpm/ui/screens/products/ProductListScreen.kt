package com.jort.stockcontrolpm.ui.screens.products

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jort.stockcontrolpm.domain.model.Product

@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onCreateProductClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearError: () -> Unit,
    onDashboardClick: () -> Unit,
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
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text(text = "Buscar producto") },
            modifier = Modifier.fillMaxWidth()
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> {
                ProductListErrorCard(
                    message = uiState.errorMessage,
                    onClearError = onClearError
                )
            }
            uiState.isEmpty -> {
                EmptyProductListCard()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { product -> product.id }
                    ) { product ->
                        ProductItemCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onCreateProductClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Nuevo producto")
        }
        OutlinedButton(
            onClick = onDashboardClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver al inicio")
        }
    }
}

@Composable
private fun EmptyProductListCard(
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
                text = "Registra un producto para comenzar a controlar el inventario.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ProductListErrorCard(
    message: String,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "No se pudo cargar el inventario",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(onClick = onClearError) {
                Text(text = "Entendido")
            }
        }
    }
}

@Composable
private fun ProductItemCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Categoria: ${product.category}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Stock: ${product.stock} | Minimo: ${product.minStock}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Valor: S/ ${product.inventoryValue}",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(onClick = onClick) {
                Text(text = "Ver detalle")
            }
        }
    }
}
