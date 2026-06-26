package com.jort.stockcontrolpm.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ProductDetailScreen(
    productId: Long,
    uiState: ProductDetailUiState,
    onEditClick: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    onClearError: () -> Unit,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            productName = uiState.product?.name ?: "",
            onConfirm   = { showDeleteDialog = false; onDeleteClick() },
            onDismiss   = { showDeleteDialog = false }
        )
    }

    Column(modifier = modifier.fillMaxSize().background(Bg)) {

        // ── Top bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(
                    start  = MaterialTheme.spacing.space2,
                    end    = MaterialTheme.spacing.space5,
                    top    = MaterialTheme.spacing.space12,
                    bottom = MaterialTheme.spacing.space3
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, "Volver", tint = TextPrimary)
            }
            Text(
                text       = "Detalle",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                modifier   = Modifier.weight(1f)
            )
            if (uiState.product != null) {
                IconButton(onClick = { onEditClick(productId) }) {
                    Icon(Icons.Outlined.Edit, "Editar", tint = Primary)
                }
            }
        }

        // ── Contenido ────────────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Primary) }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.space5),
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
                        Text("No se pudo cargar el producto",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Error, fontWeight = FontWeight.Bold)
                        Text(uiState.errorMessage, style = MaterialTheme.typography.bodySmall, color = Error)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                                .border(1.dp, Error.copy(alpha = 0.30f), RoundedCornerShape(MaterialTheme.radius.lg))
                                .clickable(onClick = onClearError)
                                .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space2)
                        ) {
                            Text("Entendido", style = MaterialTheme.typography.bodySmall,
                                color = Error, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            uiState.product != null -> {
                ProductDetailContent(
                    product         = uiState.product,
                    onEditClick     = { onEditClick(productId) },
                    onDeleteClick   = { showDeleteDialog = true },
                    modifier        = Modifier.weight(1f)
                )
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Producto no encontrado.", color = TextSecondary)
                }
            }
        }
    }
}

// ── Contenido principal del detalle ───────────────────────────────────────────
@Composable
private fun ProductDetailContent(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stockColor = when (product.stockStatus) {
        StockStatus.OK           -> Success
        StockStatus.LOW          -> Warning
        StockStatus.OUT_OF_STOCK -> Error
    }
    val stockBg = when (product.stockStatus) {
        StockStatus.OK           -> SuccessLight
        StockStatus.LOW          -> WarningLight
        StockStatus.OUT_OF_STOCK -> ErrorLight
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(MaterialTheme.spacing.space5),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
    ) {
        // ── Cabecera del producto ───────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(Surface)
                .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
            ) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                        .background(PrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Inventory2, null, tint = Primary, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(product.name, style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(product.category, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                // Badge de stock
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(MaterialTheme.radius.sm))
                        .background(stockBg)
                        .padding(horizontal = MaterialTheme.spacing.space3, vertical = 4.dp)
                ) {
                    Text(
                        text = when (product.stockStatus) {
                            StockStatus.OK           -> "OK"
                            StockStatus.LOW          -> "Stock bajo"
                            StockStatus.OUT_OF_STOCK -> "Agotado"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = stockColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (product.sku.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Outlined.QrCode, null, modifier = Modifier.size(12.dp), tint = TextMuted)
                    Text("SKU: ${product.sku}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }

        // ── Barra de stock ──────────────────────────────────────────────────
        StockBar(product = product, color = stockColor)

        // ── Precios y márgenes ──────────────────────────────────────────────
        PriceCard(product = product)

        // ── Información adicional ───────────────────────────────────────────
        InfoSection(product = product)

        // ── Acciones (sticky en fondo) ──────────────────────────────────────
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
        StickyActionBar(
            onEditClick   = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

// ── StockBar con marcador de mínimo ───────────────────────────────────────────
@Composable
private fun StockBar(product: Product, color: Color) {
    val maxStock  = maxOf(product.stock, product.minStock * 2, 1)
    val fillRatio = (product.stock.toFloat() / maxStock).coerceIn(0f, 1f)
    val minRatio  = (product.minStock.toFloat() / maxStock).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Stock actual", style = MaterialTheme.typography.bodySmall,
                color = TextSecondary, fontWeight = FontWeight.Medium)
            Text("${product.stock} / mín. ${product.minStock} und.",
                style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }

        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Divider)
        ) {
            // Relleno de stock
            Box(
                modifier = Modifier
                    .fillMaxWidth(fillRatio)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(999.dp))
                    .background(color)
            )
            // Marcador de mínimo
            Box(
                modifier = Modifier
                    .fillMaxWidth(minRatio)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(14.dp)
                        .background(Warning)
                )
            }
        }

        Text("La línea naranja indica el stock mínimo configurado.",
            style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

// ── Tarjeta de precios y margen ───────────────────────────────────────────────
@Composable
private fun PriceCard(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Text("Precios", style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PriceLine(label = "Precio de venta",  value = "S/ %.2f".format(product.unitPrice))
            if (product.purchasePrice != null) {
                PriceLine(label = "Precio de costo", value = "S/ %.2f".format(product.purchasePrice))
            }
        }

        if (product.margin != null) {
            val marginColor = if (product.margin!! >= 0) Success else Error
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                    .background(if (product.margin!! >= 0) SuccessLight else ErrorLight)
                    .padding(horizontal = MaterialTheme.spacing.space3, vertical = MaterialTheme.spacing.space2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Margen estimado", style = MaterialTheme.typography.bodySmall,
                    color = marginColor, fontWeight = FontWeight.Medium)
                Text("${"%.1f".format(product.margin)}%",
                    style = MaterialTheme.typography.headlineSmall,
                    color = marginColor, fontWeight = FontWeight.ExtraBold)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text("Valor total en inventario", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("S/ %.2f".format(product.inventoryValue), style = MaterialTheme.typography.headlineSmall,
                color = Primary, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun PriceLine(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

// ── Información adicional ─────────────────────────────────────────────────────
@Composable
private fun InfoSection(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Text("Información", style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))

        InfoRow(icon = Icons.Outlined.Category, label = "Categoría",  value = product.category)
        if (!product.supplier.isNullOrBlank()) {
            InfoRow(icon = Icons.Outlined.Person, label = "Marca / Proveedor", value = product.supplier!!)
        }
        if (!product.expirationDate.isNullOrBlank()) {
            InfoRow(icon = Icons.Outlined.DateRange, label = "Lanzamiento", value = product.expirationDate!!)
        }
        if (!product.description.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Descripción", style = MaterialTheme.typography.bodySmall,
                    color = TextMuted, fontWeight = FontWeight.Medium)
                Text(product.description!!, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(MaterialTheme.radius.sm))
                .background(Bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(value, style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Barra de acciones ─────────────────────────────────────────────────────────
@Composable
private fun StickyActionBar(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier.height(48.dp).weight(1f),
            shape  = RoundedCornerShape(MaterialTheme.radius.lg),
            border = androidx.compose.foundation.BorderStroke(1.dp, Error.copy(alpha = 0.50f))
        ) {
            Icon(Icons.Outlined.Delete, null, tint = Error, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Eliminar", color = Error, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onEditClick,
            modifier = Modifier.height(48.dp).weight(2f),
            shape  = RoundedCornerShape(MaterialTheme.radius.lg),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
        ) {
            Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Editar producto", fontWeight = FontWeight.Bold)
        }
    }
}

// ── Diálogo de confirmación ───────────────────────────────────────────────────
@Composable
private fun DeleteConfirmDialog(
    productName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text("Eliminar producto") },
        text   = { Text("¿Estás seguro de eliminar \"$productName\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = Error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
