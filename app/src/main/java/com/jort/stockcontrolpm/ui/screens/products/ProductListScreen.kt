package com.jort.stockcontrolpm.ui.screens.products

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.QrCodeScanner
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
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
import kotlinx.coroutines.delay

@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    onCreateProductClick: () -> Unit,
    onProductClick: (Long) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearError: () -> Unit,
    onDashboardClick: () -> Unit,
    onCategoryChange: ((String?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showScanner by remember { mutableStateOf(false) }

    if (showScanner) {
        ScannerDialog(
            products  = uiState.products,
            onFound   = { product ->
                showScanner = false
                onProductClick(product.id)
            },
            onDismiss = { showScanner = false }
        )
    }

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
            InventoryHeader(
                totalCount          = uiState.products.size,
                searchQuery         = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onScanClick         = { showScanner = true }
            )

            if (uiState.availableCategories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.space5,
                        vertical   = MaterialTheme.spacing.space3
                    ),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
                ) {
                    item {
                        CategoryChip(
                            label   = "Todos",
                            selected = uiState.selectedCategory == null,
                            onClick  = { onCategoryChange?.invoke(null) }
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
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))
            }

            when {
                uiState.isLoading          -> SkeletonList()
                uiState.errorMessage != null -> ErrorState(uiState.errorMessage, onClearError)
                uiState.isEmpty            -> EmptyState(onCreateProductClick)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start  = MaterialTheme.spacing.space5,
                            end    = MaterialTheme.spacing.space5,
                            top    = MaterialTheme.spacing.space4,
                            bottom = 96.dp
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

// ── Header con buscador + botón escáner ───────────────────────────────────────
@Composable
private fun InventoryHeader(
    totalCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScanClick: () -> Unit
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier      = Modifier.weight(1f),
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
                    focusedBorderColor      = Primary,
                    unfocusedBorderColor    = Divider,
                    focusedContainerColor   = Bg,
                    unfocusedContainerColor = Bg,
                    cursorColor             = Primary
                )
            )

            // Botón escáner
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                    .background(Primary)
                    .clickable(onClick = onScanClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.QrCodeScanner,
                    contentDescription = "Escanear código",
                    tint               = Color.White,
                    modifier           = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ── Diálogo simulador de escáner ──────────────────────────────────────────────
@Composable
private fun ScannerDialog(
    products: List<Product>,
    onFound: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    var phase by remember { mutableStateOf("scanning") } // scanning | found
    var detectedProduct by remember { mutableStateOf<Product?>(null) }

    // Simula el escaneo: espera 2.5 s y "detecta" un producto aleatorio con SKU
    LaunchedEffect(Unit) {
        delay(2500)
        val target = products.firstOrNull { it.sku.isNotBlank() } ?: products.firstOrNull()
        if (target != null) {
            detectedProduct = target
            phase = "found"
            delay(1200)
            onFound(target)
        } else {
            onDismiss()
        }
    }

    // Animación de la línea de escaneo
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 200f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1A1A2E))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = if (phase == "scanning") "Escaneando…" else "Código detectado",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = Color.White.copy(alpha = 0.6f))
                }
            }

            // Visor del escáner
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                // Esquinas del visor
                ScannerCorners()

                if (phase == "scanning") {
                    // Línea animada de escaneo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset(y = scanY.dp)
                            .background(
                                color = Color(0xFF00FF88).copy(alpha = 0.85f),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                    Text(
                        text     = "Apunta al código de barras\ndel producto",
                        color    = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        lineHeight = 18.sp
                    )
                } else {
                    // Estado "detectado"
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            null,
                            tint     = Color(0xFF00FF88),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text     = detectedProduct?.sku?.takeIf { it.isNotBlank() }
                                ?: "SKU-${detectedProduct?.id}",
                            color    = Color(0xFF00FF88),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (phase == "found" && detectedProduct != null) {
                Text(
                    text       = detectedProduct!!.name,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
                Text(
                    text     = "Abriendo producto…",
                    color    = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Esquinas decorativas del visor
@Composable
private fun ScannerCorners() {
    val c = Color(0xFF00FF88)
    val s = 24.dp
    val t = 3.dp
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left
        Box(Modifier.size(s).align(Alignment.TopStart)) {
            Box(Modifier.width(s).height(t).background(c))
            Box(Modifier.width(t).height(s).background(c))
        }
        // Top-right
        Box(Modifier.size(s).align(Alignment.TopEnd)) {
            Box(Modifier.width(s).height(t).background(c).align(Alignment.TopEnd))
            Box(Modifier.width(t).height(s).background(c).align(Alignment.TopEnd))
        }
        // Bottom-left
        Box(Modifier.size(s).align(Alignment.BottomStart)) {
            Box(Modifier.width(s).height(t).background(c).align(Alignment.BottomStart))
            Box(Modifier.width(t).height(s).background(c).align(Alignment.BottomStart))
        }
        // Bottom-right
        Box(Modifier.size(s).align(Alignment.BottomEnd)) {
            Box(Modifier.width(s).height(t).background(c).align(Alignment.BottomEnd))
            Box(Modifier.width(t).height(s).background(c).align(Alignment.BottomEnd))
        }
    }
}

// ── Imagen del producto ───────────────────────────────────────────────────────
// SubcomposeAsyncImage permite manejar loading/error/success con composables propios.
@Composable
private fun ProductImage(imageUrl: String?, name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryLight),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model              = imageUrl,
                contentDescription = name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color    = Primary,
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = name.take(1).uppercase(),
                            color      = Primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 22.sp
                        )
                    }
                }
            )
        } else {
            Text(
                text       = name.take(1).uppercase(),
                color      = Primary,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 22.sp
            )
        }
    }
}

// ── Tarjeta de producto ───────────────────────────────────────────────────────
@Composable
private fun ProductRow(product: Product, onClick: () -> Unit) {
    val stripColor = when (product.stockStatus) {
        StockStatus.OK           -> Success
        StockStatus.LOW          -> Warning
        StockStatus.OUT_OF_STOCK -> Error
    }
    val badgeBg    = when (product.stockStatus) {
        StockStatus.OK           -> SuccessLight
        StockStatus.LOW          -> WarningLight
        StockStatus.OUT_OF_STOCK -> ErrorLight
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Strip lateral de estado
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(88.dp)
                .background(stripColor)
        )

        ProductImage(
            imageUrl = product.imagenUrl,
            name     = product.name,
            modifier = Modifier.padding(start = 10.dp)
        )

        // Datos del producto
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start  = MaterialTheme.spacing.space3,
                    end    = MaterialTheme.spacing.space4,
                    top    = MaterialTheme.spacing.space3,
                    bottom = MaterialTheme.spacing.space3
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text       = product.name,
                    style      = MaterialTheme.typography.titleMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(6.dp))
                // Badge de stock
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(MaterialTheme.radius.sm))
                        .background(badgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = when (product.stockStatus) {
                            StockStatus.OK           -> "${product.stock} und."
                            StockStatus.LOW          -> "Bajo: ${product.stock}"
                            StockStatus.OUT_OF_STOCK -> "Agotado"
                        },
                        style      = MaterialTheme.typography.labelSmall,
                        color      = stripColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Outlined.Category, null,
                        modifier = Modifier.size(11.dp), tint = TextMuted)
                    Text(product.category,
                        style = MaterialTheme.typography.labelSmall, color = TextMuted,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("·", color = TextMuted, fontSize = 10.sp)
                Text(
                    text  = "S/ %.2f".format(product.unitPrice),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                if (product.sku.isNotBlank()) {
                    Text("·", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text  = product.sku,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

// ── Estados ───────────────────────────────────────────────────────────────────
@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Primary else Surface)
            .border(1.dp, if (selected) Primary else Divider, RoundedCornerShape(999.dp))
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

@Composable
private fun EmptyState(onCreateClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.space8),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(PrimaryLight),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Add, null, tint = Primary, modifier = Modifier.size(32.dp))
            }
            Text("Sin productos registrados", style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Registra tu primer producto para comenzar.",
                style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}

@Composable
private fun ErrorState(message: String, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.space5),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(ErrorLight)
                .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.xl))
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Text("No se pudo cargar el inventario",
                style = MaterialTheme.typography.headlineSmall, color = Error, fontWeight = FontWeight.Bold)
            Text(message, style = MaterialTheme.typography.bodySmall, color = Error)
            Box(modifier = Modifier.clip(RoundedCornerShape(MaterialTheme.radius.lg))
                .border(1.dp, Error.copy(alpha = 0.30f), RoundedCornerShape(MaterialTheme.radius.lg))
                .clickable(onClick = onDismiss)
                .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space2)) {
                Text("Entendido", style = MaterialTheme.typography.bodySmall,
                    color = Error, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SkeletonList() {
    Column(modifier = Modifier.fillMaxSize()
        .padding(horizontal = MaterialTheme.spacing.space5, vertical = MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
        repeat(5) {
            Row(modifier = Modifier.fillMaxWidth().height(88.dp)
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(Divider.copy(alpha = 0.50f)),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(4.dp).fillMaxSize().background(Divider))
                Box(Modifier.size(60.dp).padding(10.dp).clip(RoundedCornerShape(10.dp)).background(Divider))
            }
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }
}
