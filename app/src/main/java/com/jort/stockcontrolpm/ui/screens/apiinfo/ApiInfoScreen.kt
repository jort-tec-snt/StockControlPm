package com.jort.stockcontrolpm.ui.screens.apiinfo

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.jort.stockcontrolpm.domain.model.ExternalProduct
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
import com.jort.stockcontrolpm.ui.theme.radius
import com.jort.stockcontrolpm.ui.theme.spacing

@Composable
fun ApiInfoScreen(
    uiState: ApiInfoUiState,
    onRetryClick: () -> Unit,
    onClearError: () -> Unit,
    onDashboardClick: () -> Unit,
    onProductsClick: () -> Unit,
    onImportProduct: ((ExternalProduct) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Muestra confirmación al importar
    LaunchedEffect(uiState.lastImported) {
        if (uiState.lastImported != null) {
            snackbarHostState.showSnackbar("\"${uiState.lastImported}\" agregado al inventario")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Bg)) {

            // ── Top bar ──────────────────────────────────────────────────────
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
                IconButton(onClick = onDashboardClick) {
                    Icon(Icons.Outlined.ArrowBack, "Volver", tint = TextPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Catálogo externo", style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                    Text("FakeStore API · Retrofit", style = MaterialTheme.typography.bodySmall,
                        color = TextMuted)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight)
                        .clickable(enabled = !uiState.isLoading, onClick = onRetryClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp),
                            color = Primary, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Refresh, "Recargar",
                            tint = Primary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ── Contenido ────────────────────────────────────────────────────
            when {
                uiState.isLoading && !uiState.hasProducts -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                        ) {
                            CircularProgressIndicator(color = Primary)
                            Text("Descargando catálogo…",
                                style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .padding(MaterialTheme.spacing.space5),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                                .background(ErrorLight)
                                .border(1.dp, Error.copy(alpha = 0.25f),
                                    RoundedCornerShape(MaterialTheme.radius.xl))
                                .padding(MaterialTheme.spacing.space5),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                        ) {
                            Text("Sin conexión", style = MaterialTheme.typography.headlineSmall,
                                color = Error, fontWeight = FontWeight.Bold)
                            Text(uiState.errorMessage,
                                style = MaterialTheme.typography.bodySmall, color = Error)
                            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                                        .border(1.dp, Error.copy(alpha = 0.30f),
                                            RoundedCornerShape(MaterialTheme.radius.lg))
                                        .clickable(onClick = onClearError)
                                        .padding(horizontal = MaterialTheme.spacing.space4,
                                            vertical = MaterialTheme.spacing.space2)
                                ) {
                                    Text("Entendido", style = MaterialTheme.typography.bodySmall,
                                        color = Error, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                                        .background(Error)
                                        .clickable(onClick = onRetryClick)
                                        .padding(horizontal = MaterialTheme.spacing.space4,
                                            vertical = MaterialTheme.spacing.space2)
                                ) {
                                    Text("Reintentar", style = MaterialTheme.typography.bodySmall,
                                        color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                uiState.hasProducts -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = MaterialTheme.spacing.space5,
                            vertical   = MaterialTheme.spacing.space4
                        ),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                    ) {
                        item {
                            // Banner informativo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                                    .background(AccentLight)
                                    .border(1.dp, Accent.copy(alpha = 0.25f),
                                        RoundedCornerShape(MaterialTheme.radius.lg))
                                    .padding(MaterialTheme.spacing.space3),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Cloud, null,
                                    tint = Accent, modifier = Modifier.size(14.dp))
                                Text(
                                    "${uiState.products.size} productos desde fakestoreapi.com · " +
                                            "Toca + para importar al inventario local",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Accent, fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
                        }

                        items(uiState.products, key = { it.id }) { product ->
                            CatalogProductCard(
                                product       = product,
                                alreadyAdded  = uiState.importedIds.contains(product.id),
                                onImport      = { onImportProduct?.invoke(product) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8)) }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                        ) {
                            Box(
                                modifier = Modifier.size(64.dp).clip(CircleShape)
                                    .background(PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Cloud, null,
                                    tint = Primary, modifier = Modifier.size(32.dp))
                            }
                            Text("Catálogo vacío", style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Toca ↻ para cargar el catálogo externo.",
                                style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Snackbar de confirmación
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.space4)
        )
    }
}

// ── Tarjeta de producto del catálogo externo ──────────────────────────────────
@Composable
private fun CatalogProductCard(
    product: ExternalProduct,
    alreadyAdded: Boolean,
    onImport: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        // Imagen del producto
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                .background(Bg),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model             = product.imageUrl,
                contentDescription = product.title,
                contentScale      = ContentScale.Fit,
                modifier          = Modifier.size(64.dp),
                loading = {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(24.dp),
                        color       = Primary,
                        strokeWidth = 2.dp
                    )
                },
                error = {
                    Icon(Icons.Outlined.BrokenImage, null,
                        tint = TextMuted, modifier = Modifier.size(32.dp))
                }
            )
        }

        // Información
        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space1)) {
            Text(
                text     = product.title,
                style    = MaterialTheme.typography.bodyLarge,
                color    = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text  = product.category.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Text(
                text       = "$%.2f".format(product.price),
                style      = MaterialTheme.typography.headlineSmall,
                color      = Primary,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Botón importar
        if (alreadyAdded) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SuccessLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Ya importado",
                    tint = Success, modifier = Modifier.size(18.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight)
                    .clickable(onClick = onImport),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Importar",
                    tint = Primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
