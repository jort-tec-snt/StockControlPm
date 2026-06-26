package com.jort.stockcontrolpm.ui.screens.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jort.stockcontrolpm.domain.model.Product
import com.jort.stockcontrolpm.ui.theme.Accent
import com.jort.stockcontrolpm.ui.theme.AccentLight
import com.jort.stockcontrolpm.ui.theme.Bg
import com.jort.stockcontrolpm.ui.theme.Divider
import com.jort.stockcontrolpm.ui.theme.Error
import com.jort.stockcontrolpm.ui.theme.ErrorLight
import com.jort.stockcontrolpm.ui.theme.Primary
import com.jort.stockcontrolpm.ui.theme.SuccessLight
import com.jort.stockcontrolpm.ui.theme.Surface
import com.jort.stockcontrolpm.ui.theme.TextMuted
import com.jort.stockcontrolpm.ui.theme.TextPrimary
import com.jort.stockcontrolpm.ui.theme.TextSecondary
import com.jort.stockcontrolpm.ui.theme.Warning
import com.jort.stockcontrolpm.ui.theme.WarningLight
import com.jort.stockcontrolpm.ui.theme.radius
import com.jort.stockcontrolpm.ui.theme.spacing
import com.jort.stockcontrolpm.ui.theme.Success

@Composable
fun AlertsScreen(
    uiState: AlertsUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Bg)) {

        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(
                    start  = MaterialTheme.spacing.space5,
                    end    = MaterialTheme.spacing.space5,
                    top    = MaterialTheme.spacing.space12,
                    bottom = MaterialTheme.spacing.space4
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = "Alertas",
                style      = MaterialTheme.typography.headlineLarge,
                color      = TextPrimary,
                fontWeight = FontWeight.ExtraBold
            )
            if (uiState.totalAlerts > 0) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Error)
                        .padding(horizontal = MaterialTheme.spacing.space3, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "${uiState.totalAlerts}",
                        style      = MaterialTheme.typography.labelSmall,
                        color      = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            !uiState.hasAlerts -> {
                AllClearState()
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.space5,
                        vertical   = MaterialTheme.spacing.space4
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space5)
                ) {
                    // Agotados
                    if (uiState.outOfStock.isNotEmpty()) {
                        item {
                            AlertGroup(
                                icon       = Icons.Outlined.RemoveShoppingCart,
                                title      = "Agotados",
                                count      = uiState.outOfStock.size,
                                iconTint   = Error,
                                iconBg     = ErrorLight,
                                products   = uiState.outOfStock,
                                badgeColor = Error,
                                badgeBg    = ErrorLight,
                                badgeLabel = { "Stock: 0" }
                            )
                        }
                    }

                    // Stock bajo
                    if (uiState.lowStock.isNotEmpty()) {
                        item {
                            AlertGroup(
                                icon       = Icons.Outlined.Warning,
                                title      = "Stock bajo",
                                count      = uiState.lowStock.size,
                                iconTint   = Warning,
                                iconBg     = WarningLight,
                                products   = uiState.lowStock,
                                badgeColor = Warning,
                                badgeBg    = WarningLight,
                                badgeLabel = { p -> "${p.stock}/${p.minStock} und." }
                            )
                        }
                    }

                    // Por vencer
                    if (uiState.expiringSoon.isNotEmpty()) {
                        item {
                            AlertGroup(
                                icon       = Icons.Outlined.DateRange,
                                title      = "Próximos a discontinuar",
                                count      = uiState.expiringSoon.size,
                                iconTint   = Accent,
                                iconBg     = AccentLight,
                                products   = uiState.expiringSoon,
                                badgeColor = Accent,
                                badgeBg    = AccentLight,
                                badgeLabel = { p -> p.expirationDate ?: "—" }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8)) }
                }
            }
        }
    }
}

// ── Grupo de alertas ──────────────────────────────────────────────────────────
@Composable
private fun AlertGroup(
    icon: ImageVector,
    title: String,
    count: Int,
    iconTint: Color,
    iconBg: Color,
    products: List<Product>,
    badgeColor: Color,
    badgeBg: Color,
    badgeLabel: (Product) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
        // Cabecera del grupo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(MaterialTheme.radius.md))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Text(
                text       = title,
                style      = MaterialTheme.typography.headlineSmall,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(iconBg)
                    .padding(horizontal = MaterialTheme.spacing.space3, vertical = 3.dp)
            ) {
                Text("$count", style = MaterialTheme.typography.labelSmall,
                    color = iconTint, fontWeight = FontWeight.ExtraBold)
            }
        }

        // Tarjeta de productos
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(Surface)
                .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
        ) {
            products.forEachIndexed { index, product ->
                AlertProductRow(
                    product    = product,
                    badgeLabel = badgeLabel(product),
                    badgeColor = badgeColor,
                    badgeBg    = badgeBg
                )
                if (index < products.lastIndex) {
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp)
                        .padding(horizontal = MaterialTheme.spacing.space4)
                        .background(Divider))
                }
            }
        }
    }
}

// ── Fila de producto dentro del grupo ────────────────────────────────────────
@Composable
private fun AlertProductRow(
    product: Product,
    badgeLabel: String,
    badgeColor: Color,
    badgeBg: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.space4,
                vertical   = MaterialTheme.spacing.space3
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = product.name,
                style      = MaterialTheme.typography.bodyLarge,
                color      = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text  = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.space3))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.radius.sm))
                .background(badgeBg)
                .padding(horizontal = MaterialTheme.spacing.space2, vertical = 3.dp)
        ) {
            Text(badgeLabel, style = MaterialTheme.typography.labelSmall,
                color = badgeColor, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Estado sin alertas ────────────────────────────────────────────────────────
@Composable
private fun AllClearState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(SuccessLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CheckCircle, null,
                    tint = Success, modifier = Modifier.size(40.dp))
            }
            Text("Todo en orden", style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary, fontWeight = FontWeight.ExtraBold)
            Text("No hay productos con alertas activas\nen este momento.",
                style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}
