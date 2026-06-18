package com.jort.stockcontrolpm.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun DashboardScreen(
    uiState: DashboardUiState,
    onClearError: () -> Unit,
    onProductsClick: () -> Unit,
    onApiInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Resumen operativo del inventario local.",
            style = MaterialTheme.typography.bodyLarge
        )

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No se pudo cargar el dashboard",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = uiState.errorMessage)
                        OutlinedButton(onClick = onClearError) {
                            Text(text = "Entendido")
                        }
                    }
                }
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardMetricCard(
                        title = "Productos",
                        value = uiState.totalProducts.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = "Agotados",
                        value = uiState.outOfStockProducts.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardMetricCard(
                        title = "Criticos",
                        value = uiState.criticalStockProducts.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = "Por vencer",
                        value = uiState.expiringSoonProducts.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                DashboardMetricCard(
                    title = "Inventario valorizado",
                    value = "S/ %.2f".format(uiState.inventoryValue),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onProductsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver productos")
        }
        OutlinedButton(
            onClick = onApiInfoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver informacion API")
        }
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
