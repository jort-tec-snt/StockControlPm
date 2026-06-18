package com.jort.stockcontrolpm.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun ProductFormScreen(
    uiState: ProductFormUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onMinStockChange: (String) -> Unit,
    onUnitPriceChange: (String) -> Unit,
    onExpirationDateChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onClearError: () -> Unit,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (uiState.isEditing) "Editar producto" else "Nuevo producto"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Formulario base para registro y mantenimiento de productos.",
            style = MaterialTheme.typography.bodyLarge
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        if (uiState.errorMessage != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Revisa el formulario",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = uiState.errorMessage)
                    OutlinedButton(onClick = onClearError) {
                        Text(text = "Entendido")
                    }
                }
            }
        }

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(text = "Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.category,
            onValueChange = onCategoryChange,
            label = { Text(text = "Categoria") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.stock,
            onValueChange = onStockChange,
            label = { Text(text = "Stock") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.minStock,
            onValueChange = onMinStockChange,
            label = { Text(text = "Stock minimo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.unitPrice,
            onValueChange = onUnitPriceChange,
            label = { Text(text = "Precio") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.expirationDate,
            onValueChange = onExpirationDateChange,
            label = { Text(text = "Fecha de vencimiento") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && !uiState.isSaving
        ) {
            Text(
                text = when {
                    uiState.isSaving -> "Guardando..."
                    uiState.isEditing -> "Guardar cambios"
                    else -> "Registrar producto"
                }
            )
        }
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
        OutlinedButton(
            onClick = onDashboardClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver al inicio")
        }
    }
}
