package com.jort.stockcontrolpm.ui.screens.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
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
import androidx.compose.ui.unit.sp
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
fun DashboardScreen(
    uiState: DashboardUiState,
    onClearError: () -> Unit,
    onProductsClick: () -> Unit,
    onApiInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        // ── Header (fondo primary) ────────────────────────────────────────────
        DashboardHeader(
            salesToday        = uiState.salesToday,
            transactionsToday = uiState.transactionsToday
        )

        // ── Contenido scrollable ──────────────────────────────────────────────
        // weight(1f) da al área el espacio restante después del header;
        // fillMaxSize() dentro de verticalScroll rompe el scroll en Compose.
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Bg)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space6)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Primary) }
                }
                uiState.errorMessage != null -> {
                    ErrorBanner(message = uiState.errorMessage, onDismiss = onClearError)
                }
                else -> {
                    // Alert Banner condicional
                    if (uiState.alertLevel != AlertLevel.NONE) {
                        AlertBanner(uiState = uiState, onProductsClick = onProductsClick)
                    }

                    // KPIs
                    KpiSection(uiState = uiState)

                    // Próximos a vencer
                    if (uiState.expiringProductsList.isNotEmpty()) {
                        ExpiringSection(items = uiState.expiringProductsList)
                    }

                    // Acciones rápidas
                    QuickActionsSection(
                        onNewProductClick   = onProductsClick,
                        onAlertsClick       = onProductsClick,
                        onApiInfoClick      = onApiInfoClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8))
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun DashboardHeader(
    salesToday: Double,
    transactionsToday: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(
                start  = MaterialTheme.spacing.space5,
                end    = MaterialTheme.spacing.space5,
                top    = MaterialTheme.spacing.space12,
                bottom = MaterialTheme.spacing.space4
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "Buenos días",
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = Color.White.copy(alpha = 0.75f)
                )
                Text(
                    text       = "Minimarket El Progreso",
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector        = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint               = Color.White,
                modifier           = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))

        // Barra de ventas del día
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space3),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HeaderStat(label = "Ventas hoy",    value = "S/ %.2f".format(salesToday))
            VerticalDivider()
            HeaderStat(label = "Transacciones", value = transactionsToday.toString())
            VerticalDivider()
            HeaderStat(label = "Ticket prom.",  value = if (transactionsToday > 0)
                "S/ %.2f".format(salesToday / transactionsToday) else "—")
        }
    }
}

@Composable
private fun HeaderStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall,
            color = Color.White, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.70f))
    }
}

@Composable
private fun VerticalDivider() {
    Box(modifier = Modifier
        .width(1.dp).height(32.dp)
        .background(Color.White.copy(alpha = 0.20f)))
}

// ── Alert Banner ──────────────────────────────────────────────────────────────
@Composable
private fun AlertBanner(uiState: DashboardUiState, onProductsClick: () -> Unit) {
    val isCritical = uiState.alertLevel == AlertLevel.CRITICAL
    val bg         = if (isCritical) ErrorLight  else AccentLight
    val border     = if (isCritical) Error       else Accent
    val text       = if (isCritical)
        "${uiState.outOfStockProducts} producto(s) agotado(s) — reabastece urgente"
    else
        "${uiState.criticalStockProducts} producto(s) con stock bajo — planifica reposición"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.md))
            .background(bg)
            .border(1.dp, border.copy(alpha = 0.30f), RoundedCornerShape(MaterialTheme.radius.md))
            .clickable { onProductsClick() }
            .padding(horizontal = MaterialTheme.spacing.space4, vertical = MaterialTheme.spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        // Dot pulsante (simplificado sin animación para MVP)
        Box(modifier = Modifier.size(8.dp).clip(CircleShape)
            .background(if (isCritical) Error else Accent))
        Text(text = text, style = MaterialTheme.typography.bodySmall,
            color = if (isCritical) Error else Accent,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, null,
            tint = if (isCritical) Error else Accent, modifier = Modifier.size(16.dp))
    }
}

// ── KPI Grid ──────────────────────────────────────────────────────────────────
@Composable
private fun KpiSection(uiState: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            KpiCard(
                value    = uiState.totalProducts.toString(),
                label    = "Total productos",
                sub      = "en inventario",
                bg       = Surface,
                valueColor = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                value    = "S/ %,.0f".format(uiState.inventoryValue),
                label    = "Valor inventario",
                sub      = "estimado",
                bg       = Surface,
                valueColor = Primary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            KpiCard(
                value      = uiState.criticalStockProducts.toString(),
                label      = "Stock crítico",
                sub        = "por debajo del mínimo",
                bg         = if (uiState.criticalStockProducts > 0) AccentLight else Surface,
                valueColor = if (uiState.criticalStockProducts > 0) Accent else TextPrimary,
                modifier   = Modifier.weight(1f)
            )
            KpiCard(
                value      = uiState.outOfStockProducts.toString(),
                label      = "Agotados",
                sub        = "stock = 0",
                bg         = if (uiState.outOfStockProducts > 0) ErrorLight else Surface,
                valueColor = if (uiState.outOfStockProducts > 0) Error else TextPrimary,
                modifier   = Modifier.weight(1f)
            )
        }
        KpiCard(
            value      = uiState.expiringSoonProducts.toString(),
            label      = "Próximos a vencer",
            sub        = "en los próximos 10 días",
            bg         = if (uiState.expiringSoonProducts > 0) AccentLight else Surface,
            valueColor = if (uiState.expiringSoonProducts > 0) Warning else TextPrimary,
            modifier   = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun KpiCard(
    value: String,
    label: String,
    sub: String,
    bg: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(bg)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space1)
    ) {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        Text(text = label, style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = sub, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

// ── Próximos a vencer ─────────────────────────────────────────────────────────
@Composable
private fun ExpiringSection(items: List<ExpiringProductInfo>) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
        Text(text = "Próximos a vencer", style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                    .background(Surface)
                    .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.lg))
                    .padding(MaterialTheme.spacing.space4),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary, fontWeight = FontWeight.Medium)
                DaysBadge(days = item.daysLeft)
            }
        }
    }
}

@Composable
private fun DaysBadge(days: Int) {
    val bg    = if (days <= 3) ErrorLight else AccentLight
    val color = if (days <= 3) Error      else Warning
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = MaterialTheme.spacing.space3, vertical = 4.dp)
    ) {
        Text(text = "${days}d", style = MaterialTheme.typography.labelSmall,
            color = color, fontWeight = FontWeight.Bold)
    }
}

// ── Acciones rápidas ──────────────────────────────────────────────────────────
@Composable
private fun QuickActionsSection(
    onNewProductClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onApiInfoClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)) {
        Text(text = "Acciones rápidas", style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            QuickActionTile(
                icon    = Icons.Outlined.Add,
                label   = "Nuevo producto",
                onClick = onNewProductClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon    = Icons.Outlined.ArrowDownward,
                label   = "Reg. entrada",
                onClick = onNewProductClick,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            QuickActionTile(
                icon    = Icons.Outlined.ArrowUpward,
                label   = "Reg. salida",
                onClick = onNewProductClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                icon    = Icons.Outlined.NotificationsActive,
                label   = "Ver alertas",
                onClick = onAlertsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .clickable(onClick = onClick)
            .padding(MaterialTheme.spacing.space4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(MaterialTheme.radius.md))
                .background(PrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Primary, modifier = Modifier.size(20.dp))
        }
        Text(text = label, style = MaterialTheme.typography.bodySmall,
            color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

// ── Error banner ──────────────────────────────────────────────────────────────
@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.lg))
            .background(ErrorLight)
            .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.lg))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
    ) {
        Text("No se pudo cargar el dashboard", style = MaterialTheme.typography.headlineSmall,
            color = Error, fontWeight = FontWeight.Bold)
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
