package com.jort.stockcontrolpm.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jort.stockcontrolpm.domain.model.Product
import com.jort.stockcontrolpm.domain.model.StockStatus
import com.jort.stockcontrolpm.ui.theme.Accent
import com.jort.stockcontrolpm.ui.theme.AccentLight
import com.jort.stockcontrolpm.ui.theme.Bg
import com.jort.stockcontrolpm.ui.theme.Divider
import com.jort.stockcontrolpm.ui.theme.Error
import com.jort.stockcontrolpm.ui.theme.ErrorLight
import com.jort.stockcontrolpm.ui.theme.Primary
import com.jort.stockcontrolpm.ui.theme.PrimaryLight
import com.jort.stockcontrolpm.ui.theme.Success
import com.jort.stockcontrolpm.ui.theme.SuccessLight
import com.jort.stockcontrolpm.ui.theme.Surface
import com.jort.stockcontrolpm.ui.theme.TextMuted
import com.jort.stockcontrolpm.ui.theme.TextPrimary
import com.jort.stockcontrolpm.ui.theme.TextSecondary
import com.jort.stockcontrolpm.ui.theme.Warning
import com.jort.stockcontrolpm.ui.theme.WarningLight
import com.jort.stockcontrolpm.ui.theme.radius
import com.jort.stockcontrolpm.ui.theme.spacing

@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onCreateProductClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSyncFromApi: () -> Unit,
    onClearError: () -> Unit,
    onDashboardClick: () -> Unit,
    onCategoryChange: ((String?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Bg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateProductClick,
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("Nuevo producto", fontWeight = FontWeight.Bold) },
                containerColor = Primary,
                contentColor = Color.White
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Header ──────────────────────────────────────────────────────────
            InventoryHeader(
                totalCount   = uiState.products.size,
                searchQuery  = uiState.searchQuery,
                isSyncingApi = uiState.isSyncingApi,
                apiSyncError = uiState.apiSyncError,
                lastImportedCount = uiState.lastImportedCount,
                onSearchQueryChange = onSearchQueryChange,
                onSyncFromApi = onSyncFromApi
            )

            // ── Category chips ───────────────────────────────────────────────────
            if (uiState.availableCategories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.space5,
                        vertical   = MaterialTheme.spacing.space3
                    ),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
                ) {
                    // Chip "Todos"
                    item {
                        CategoryChip(
                            label      = "Todos",
                            selected   = uiState.selectedCategory == null,
                            onClick    = { onCategoryChange?.invoke(null) }
                        )
                    }
                    items(uiState.availableCategories) { cat ->
                        CategoryChip(
                            label    = cat,
                            selected = uiState.selectedCategory == cat,
                            onClick  = { onCategoryChange?.invoke(cat) }
                        )
                    }
                }
                // Separador
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Divider)
                )
            }

            // ── Lista / estados ──────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    SkeletonList()
                }
                uiState.errorMessage != null -> {
                    ErrorState(message = uiState.errorMessage, onDismiss = onClearError)
                }
                uiState.isEmpty -> {
                    EmptyState(onCreateClick = onCreateProductClick)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start  = MaterialTheme.spacing.space5,
                            end    = MaterialTheme.spacing.space5,
                            top    = MaterialTheme.spacing.space4,
                            bottom = 96.dp   // espacio para el FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                    ) {
                        items(uiState.products, key = { it.id }) { product ->
                            ProductRow(product = product, onClick = { onProductClick(product.id) })
                        }
                    }
                }
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun InventoryHeader(
    totalCount: Int,
    searchQuery: String,
    isSyncingApi: Boolean,
    apiSyncError: String?,
    lastImportedCount: Int?,
    onSearchQueryChange: (String) -> Unit,
    onSyncFromApi: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(
                start  = MaterialTheme.spacing.space5,
                end    = MaterialTheme.spacing.space5,
                top    = MaterialTheme.spacing.space12,
                bottom = MaterialTheme.spacing.space4
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Text(
                text       = "Inventario",
                style      = MaterialTheme.typography.headlineLarge,
                color      = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                modifier   = Modifier.weight(1f)
            )
            IconButton(onClick = onSyncFromApi, enabled = !isSyncingApi) {
                if (isSyncingApi) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = "Sincronizar con FakeStore API",
                        tint = Primary
                    )
                }
            }
            // Badge con conteo
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryLight)
                    .padding(horizontal = MaterialTheme.spacing.space3, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "$totalCount",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = Primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Text(
            text = when {
                isSyncingApi -> "Sincronizando productos desde FakeStore API…"
                apiSyncError != null -> "No se pudo sincronizar la API: $apiSyncError"
                lastImportedCount != null && lastImportedCount > 0 ->
                    "$lastImportedCount productos nuevos importados desde la API"
                lastImportedCount == 0 -> "Inventario sincronizado con FakeStore API"
                else -> "Inventario local y catálogo externo"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (apiSyncError != null) Error else TextMuted,
            maxLines = 2
        )

        // Barra de búsqueda
        OutlinedTextField(
            value         = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = { Text("Buscar por nombre o categoría…", color = TextMuted) },
            leadingIcon   = { Icon(Icons.Outlined.Search, null, tint = TextMuted) },
            trailingIcon  = if (searchQuery.isNotBlank()) {{
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Outlined.Close, "Limpiar", tint = TextMuted)
                }
            }} else null,
            singleLine    = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape  = RoundedCornerShape(MaterialTheme.radius.xl),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Primary,
                unfocusedBorderColor = Divider,
                focusedContainerColor   = Bg,
                unfocusedContainerColor = Bg,
                cursorColor          = Primary
            )
        )
    }
}

// ── Category chip ─────────────────────────────────────────────────────────────
@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Primary else Surface)
            .border(
                1.dp,
                if (selected) Primary else Divider,
                RoundedCornerShape(999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = MaterialTheme.spacing.space4, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color      = if (selected) Color.White else TextSecondary
        )
    }
}

// ── Fila de producto con StockStrip ───────────────────────────────────────────
@Composable
private fun ProductRow(product: Product, onClick: () -> Unit) {
    val stripColor = when (product.stockStatus) {
        StockStatus.OK          -> Success
        StockStatus.LOW         -> Warning
        StockStatus.OUT_OF_STOCK -> Error
    }
    val badgeBg = when (product.stockStatus) {
        StockStatus.OK          -> SuccessLight
        StockStatus.LOW         -> WarningLight
        StockStatus.OUT_OF_STOCK -> ErrorLight
    }
    val badgeColor = stripColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .clickable(onClick = onClick)
    ) {
        // Strip lateral de estado de stock
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(80.dp)
                .background(stripColor)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start  = MaterialTheme.spacing.space4,
                    end    = MaterialTheme.spacing.space4,
                    top    = MaterialTheme.spacing.space3,
                    bottom = MaterialTheme.spacing.space3
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space1)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text       = product.name,
                    style      = MaterialTheme.typography.headlineSmall,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.space2))
                StockBadge(product = product, bg = badgeBg, color = badgeColor)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.Category, null,
                        modifier = Modifier.size(12.dp), tint = TextMuted)
                    Text(product.category, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Text("•", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                Text(
                    text  = "S/ %.2f".format(product.unitPrice),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StockBadge(product: Product, bg: Color, color: Color) {
    val label = when (product.stockStatus) {
        StockStatus.OK          -> "${product.stock} und."
        StockStatus.LOW         -> "Stock bajo: ${product.stock}"
        StockStatus.OUT_OF_STOCK -> "Agotado"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.radius.sm))
            .background(bg)
            .padding(horizontal = MaterialTheme.spacing.space2, vertical = 3.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall,
            color = color, fontWeight = FontWeight.Bold)
    }
}

// ── Estados vacío / error / skeleton ─────────────────────────────────────────
@Composable
private fun EmptyState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.space8),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, null, tint = Primary, modifier = Modifier.size(32.dp))
            }
            Text("Sin productos registrados", style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Registra tu primer producto para\ncomenzar a controlar el inventario.",
                style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}

@Composable
private fun ErrorState(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.space5),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(ErrorLight)
                .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.xl))
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Text("No se pudo cargar el inventario",
                style = MaterialTheme.typography.headlineSmall, color = Error, fontWeight = FontWeight.Bold)
            Text(message, style = MaterialTheme.typography.bodySmall, color = Error)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                    .border(1.dp, Error.copy(alpha = 0.30f), RoundedCornerShape(MaterialTheme.radius.lg))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space2)
            ) {
                Text("Entendido", style = MaterialTheme.typography.bodySmall,
                    color = Error, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SkeletonList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = MaterialTheme.spacing.space5,
                vertical   = MaterialTheme.spacing.space4
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        repeat(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                    .background(Divider.copy(alpha = 0.50f))
            )
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }
}
