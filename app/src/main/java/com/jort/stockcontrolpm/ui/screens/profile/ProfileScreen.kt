package com.jort.stockcontrolpm.ui.screens.profile

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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.jort.stockcontrolpm.ui.theme.Bg
import com.jort.stockcontrolpm.ui.theme.Divider
import com.jort.stockcontrolpm.ui.theme.Error
import com.jort.stockcontrolpm.ui.theme.ErrorLight
import com.jort.stockcontrolpm.ui.theme.Primary
import com.jort.stockcontrolpm.ui.theme.PrimaryLight
import com.jort.stockcontrolpm.ui.theme.Surface
import com.jort.stockcontrolpm.ui.theme.TextMuted
import com.jort.stockcontrolpm.ui.theme.TextPrimary
import com.jort.stockcontrolpm.ui.theme.TextSecondary
import com.jort.stockcontrolpm.ui.theme.radius
import com.jort.stockcontrolpm.ui.theme.spacing

@Composable
fun ProfileScreen(
    userName: String = "Administrador",
    userEmail: String = "admin@minimarket.pe",
    userRole: String = "Propietario",
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title  = { Text("Cerrar sesión") },
            text   = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Cerrar sesión", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
    ) { // fillMaxSize + verticalScroll OK aquí porque no hay Column padre fijo
        // ── Header ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(
                    start  = MaterialTheme.spacing.space5,
                    end    = MaterialTheme.spacing.space5,
                    top    = MaterialTheme.spacing.space12,
                    bottom = MaterialTheme.spacing.space6
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = userName.take(1).uppercase(),
                    fontSize   = 32.sp,
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = userName,
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(userEmail, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            }

            // Badge de rol
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PrimaryLight)
                    .padding(horizontal = MaterialTheme.spacing.space4, vertical = 6.dp)
            ) {
                Text(userRole, style = MaterialTheme.typography.bodySmall,
                    color = Primary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
        ) {

            // ── Sección: Cuenta ────────────────────────────────────────────
            ProfileSection(title = "Cuenta") {
                ProfileRow(
                    icon    = Icons.Outlined.Person,
                    label   = "Información personal",
                    sub     = "Nombre y correo",
                    onClick = {}
                )
                RowDivider()
                ProfileRow(
                    icon    = Icons.Outlined.Security,
                    label   = "Seguridad",
                    sub     = "Contraseña y acceso",
                    onClick = {}
                )
            }

            // ── Sección: Negocio ───────────────────────────────────────────
            ProfileSection(title = "Negocio") {
                ProfileRow(
                    icon    = Icons.Outlined.Inventory2,
                    label   = "Minimarket El Progreso",
                    sub     = "Configuración del local",
                    onClick = {}
                )
            }

            // ── Sección: Aplicación ────────────────────────────────────────
            ProfileSection(title = "Aplicación") {
                ProfileRow(
                    icon    = Icons.Outlined.Info,
                    label   = "Versión",
                    sub     = "STOCKCONTROL v2.4 MVP",
                    showChevron = false,
                    onClick = {}
                )
                RowDivider()
                ProfileRow(
                    icon    = Icons.Outlined.Code,
                    label   = "Desarrollado por",
                    sub     = "Ortiz Jeronimo — TECSUP 2026",
                    showChevron = false,
                    onClick = {}
                )
            }

            // ── Botón cerrar sesión ────────────────────────────────────────
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                    .background(ErrorLight)
                    .border(0.5.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.xl))
                    .clickable { showLogoutDialog = true }
                    .padding(MaterialTheme.spacing.space4),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
            ) {
                Icon(Icons.Outlined.Logout, null, tint = Error, modifier = Modifier.size(20.dp))
                Text(
                    text       = "Cerrar sesión",
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = Error,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8))
        }
    }
}

// ── Sección con tarjeta ───────────────────────────────────────────────────────
@Composable
private fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)) {
        Text(
            text       = title.uppercase(),
            style      = MaterialTheme.typography.labelSmall,
            color      = TextMuted,
            modifier   = Modifier.padding(horizontal = MaterialTheme.spacing.space2)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.xl))
                .background(Surface)
                .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
        ) {
            content()
        }
    }
}

// ── Fila de configuración ─────────────────────────────────────────────────────
@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    sub: String,
    onClick: () -> Unit,
    showChevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.spacing.space4,
                vertical   = MaterialTheme.spacing.space4
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(MaterialTheme.radius.md))
                .background(PrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        if (showChevron) {
            Icon(Icons.Outlined.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .padding(start = (36 + 12 + 16).dp)
            .background(Divider)
    )
}
