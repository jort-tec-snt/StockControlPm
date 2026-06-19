package com.jort.stockcontrolpm.ui.screens.pos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jort.stockcontrolpm.domain.model.Product
import com.jort.stockcontrolpm.ui.theme.Accent
import com.jort.stockcontrolpm.ui.theme.AccentLight
import com.jort.stockcontrolpm.ui.theme.Bg
import com.jort.stockcontrolpm.ui.theme.Divider
import com.jort.stockcontrolpm.ui.theme.Primary
import com.jort.stockcontrolpm.ui.theme.PrimaryLight
import com.jort.stockcontrolpm.ui.theme.Success
import com.jort.stockcontrolpm.ui.theme.SuccessLight
import com.jort.stockcontrolpm.ui.theme.Surface
import com.jort.stockcontrolpm.ui.theme.TextMuted
import com.jort.stockcontrolpm.ui.theme.TextPrimary
import com.jort.stockcontrolpm.ui.theme.TextSecondary
import com.jort.stockcontrolpm.ui.theme.radius
import com.jort.stockcontrolpm.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    uiState: PosUiState,
    onSearchQueryChange: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrementQty: (Long) -> Unit,
    onDecrementQty: (Long) -> Unit,
    onRemoveFromCart: (Long) -> Unit,
    onClearCart: () -> Unit,
    onCheckout: () -> Unit,
    onDismissQrSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    // QR / Confirmación de venta
    if (uiState.showQrSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onDismissQrSheet,
            sheetState       = sheetState,
            containerColor   = Surface
        ) {
            SaleSuccessSheet(
                ref       = uiState.lastSaleRef ?: "",
                total     = uiState.total,
                onDismiss = onDismissQrSheet
            )
        }
    }

    Column(modifier = modifier.fillMaxSize().background(Bg)) {

        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(
                    start  = MaterialTheme.spacing.space5,
                    end    = MaterialTheme.spacing.space5,
                    top    = MaterialTheme.spacing.space12,
                    bottom = MaterialTheme.spacing.space4
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Punto de Venta", style = MaterialTheme.typography.headlineMedium,
                    color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text("Registra una venta rápida", style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f))
            }
            // Badge carrito
            Box {
                Icon(Icons.Outlined.ShoppingCart, null,
                    tint = Color.White, modifier = Modifier.size(28.dp))
                if (uiState.cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Accent)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${uiState.cartCount}", fontSize = 10.sp,
                            color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space4))

            // ── Buscador ────────────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("Buscar producto para agregar…", color = TextMuted) },
                leadingIcon   = {
                    if (uiState.isSearching)
                        CircularProgressIndicator(modifier = Modifier.size(18.dp),
                            color = Primary, strokeWidth = 2.dp)
                    else
                        Icon(Icons.Outlined.Search, null, tint = TextMuted)
                },
                trailingIcon  = if (uiState.searchQuery.isNotBlank()) {{
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Outlined.Close, null, tint = TextMuted)
                    }
                }} else null,
                singleLine    = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape  = RoundedCornerShape(MaterialTheme.radius.xl),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Primary,
                    unfocusedBorderColor    = Divider,
                    focusedContainerColor   = Surface,
                    unfocusedContainerColor = Surface,
                    cursorColor             = Primary
                )
            )

            // ── Resultados de búsqueda ───────────────────────────────────────
            if (uiState.searchResults.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                        .background(Surface)
                        .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl)),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    uiState.searchResults.forEachIndexed { index, product ->
                        SearchResultRow(
                            product = product,
                            onClick = { onAddToCart(product) }
                        )
                        if (index < uiState.searchResults.lastIndex) {
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Divider))
                        }
                    }
                }
            }

            // ── Carrito ──────────────────────────────────────────────────────
            if (!uiState.isEmpty) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Carrito", style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onClearCart, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, "Vaciar carrito",
                            tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(uiState.cartItems, key = { it.productId }) { item ->
                        CartRow(
                            item        = item,
                            onIncrement = { onIncrementQty(item.productId) },
                            onDecrement = { onDecrementQty(item.productId) },
                            onRemove    = { onRemoveFromCart(item.productId) }
                        )
                    }
                }

                // ── Resumen y cobro ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                        .background(Surface)
                        .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
                        .padding(MaterialTheme.spacing.space4),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
                ) {
                    TotalRow("Subtotal",     "S/ %.2f".format(uiState.subtotal), TextSecondary)
                    TotalRow("IGV (18%)",    "S/ %.2f".format(uiState.igv),      TextSecondary)
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))
                    TotalRow("TOTAL",        "S/ %.2f".format(uiState.total),    TextPrimary,
                        bold = true, valueFontSize = 20)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
                    Button(
                        onClick  = onCheckout,
                        enabled  = !uiState.isProcessing,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(MaterialTheme.radius.lg),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor   = Color.White,
                            disabledContainerColor = Primary.copy(alpha = 0.45f)
                        )
                    ) {
                        if (uiState.isProcessing) {
                            CircularProgressIndicator(color = Color.White,
                                modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.space2))
                            Text("Procesando…", fontWeight = FontWeight.Bold)
                        } else {
                            Text("Cobrar  S/ %.2f".format(uiState.total), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (uiState.searchQuery.isBlank()) {
                // Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
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
                            Icon(Icons.Outlined.ShoppingCart, null,
                                tint = Primary, modifier = Modifier.size(32.dp))
                        }
                        Text("Carrito vacío", style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Busca un producto arriba para\nagregar al carrito.",
                            style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space4))
        }
    }
}

// ── Fila resultado de búsqueda ────────────────────────────────────────────────
@Composable
private fun SearchResultRow(product: Product, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space3),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary, fontWeight = FontWeight.Medium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("S/ %.2f • Stock: ${product.stock}".format(product.price),
                style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (product.stock > 0) PrimaryLight else Divider),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Add, null,
                tint = if (product.stock > 0) Primary else TextMuted,
                modifier = Modifier.size(16.dp))
        }
    }
}

// ── Fila del carrito ──────────────────────────────────────────────────────────
@Composable
private fun CartRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.lg))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.lg))
            .padding(
                horizontal = MaterialTheme.spacing.space4,
                vertical   = MaterialTheme.spacing.space3
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary, fontWeight = FontWeight.Medium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("S/ %.2f c/u".format(item.unitPrice),
                style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }

        // Controles de cantidad
        Row(verticalAlignment = Alignment.CenterVertically) {
            QtyButton(icon = Icons.Outlined.Remove, onClick = onDecrement)
            Text("${item.qty}", style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            QtyButton(
                icon    = Icons.Outlined.Add,
                onClick = onIncrement,
                enabled = item.qty < item.maxStock
            )
        }

        Text("S/ %.2f".format(item.subtotal),
            style = MaterialTheme.typography.bodySmall,
            color = Primary, fontWeight = FontWeight.Bold,
            modifier = Modifier.width(52.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End)

        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Outlined.Close, "Eliminar", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun QtyButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (enabled) PrimaryLight else Divider)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null,
            tint = if (enabled) Primary else TextMuted,
            modifier = Modifier.size(14.dp))
    }
}

// ── Total row ─────────────────────────────────────────────────────────────────
@Composable
private fun TotalRow(
    label: String,
    value: String,
    labelColor: Color,
    bold: Boolean = false,
    valueFontSize: Int = 14
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = labelColor, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = valueFontSize.sp,
            color = if (bold) Primary else labelColor,
            fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Medium)
    }
}

// ── Sheet confirmación de venta ───────────────────────────────────────────────
@Composable
private fun SaleSuccessSheet(ref: String, total: Double, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start  = MaterialTheme.spacing.space5,
                end    = MaterialTheme.spacing.space5,
                bottom = MaterialTheme.spacing.space8,
                top    = MaterialTheme.spacing.space4
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
    ) {
        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(SuccessLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.CheckCircle, null,
                tint = Success, modifier = Modifier.size(40.dp))
        }
        Text("¡Venta registrada!", style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary, fontWeight = FontWeight.ExtraBold)
        Text("La venta ha sido procesada correctamente.",
            style = MaterialTheme.typography.bodyLarge, color = TextSecondary)

        // Referencia
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(Bg)
                .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
                .padding(MaterialTheme.spacing.space4),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
        ) {
            Text("Referencia de venta", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(ref, style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary, fontWeight = FontWeight.ExtraBold)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total cobrado", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("S/ %.2f".format(total), style = MaterialTheme.typography.headlineSmall,
                    color = Primary, fontWeight = FontWeight.ExtraBold)
            }
            Text("Integración QR con Yape/Plin disponible en v2.5",
                style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }

        Button(
            onClick  = onDismiss,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(MaterialTheme.radius.lg),
            colors   = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
        ) {
            Text("Nueva venta", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
    }
}
