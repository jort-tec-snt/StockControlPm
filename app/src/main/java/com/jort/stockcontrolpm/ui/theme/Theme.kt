package com.jort.stockcontrolpm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ── Color scheme fijo STOCKCONTROL (sin dynamic color ni dark mode en MVP) ─
private val StockColorScheme = lightColorScheme(
    primary          = Primary,
    onPrimary        = Surface,
    primaryContainer = PrimaryLight,
    secondary        = Accent,
    onSecondary      = TextPrimary,
    background       = Bg,
    surface          = Surface,
    onBackground     = TextPrimary,
    onSurface        = TextPrimary,
    error            = Error,
    onError          = Surface
)

// ── Tokens de espaciado (8pt grid) ────────────────────────────────────────
data class StockSpacing(
    val space1: Dp  = 4.dp,
    val space2: Dp  = 8.dp,
    val space3: Dp  = 12.dp,
    val space4: Dp  = 16.dp,
    val space5: Dp  = 20.dp,
    val space6: Dp  = 24.dp,
    val space8: Dp  = 32.dp,
    val space12: Dp = 48.dp
)

// ── Tokens de radio de borde ──────────────────────────────────────────────
data class StockRadius(
    val xs:   Dp = 4.dp,
    val sm:   Dp = 8.dp,
    val md:   Dp = 10.dp,
    val lg:   Dp = 12.dp,
    val xl:   Dp = 14.dp,
    val xl2:  Dp = 16.dp
)

// Composition locals para acceder a tokens desde cualquier Composable
val LocalSpacing = staticCompositionLocalOf { StockSpacing() }
val LocalRadius  = staticCompositionLocalOf { StockRadius() }

@Composable
fun StockControlPmTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalSpacing provides StockSpacing(),
        LocalRadius  provides StockRadius()
    ) {
        MaterialTheme(
            colorScheme = StockColorScheme,
            typography  = Typography,
            content     = content
        )
    }
}

// Accesos rápidos desde cualquier Composable:
// MaterialTheme.spacing.space4
// MaterialTheme.radius.lg
val MaterialTheme.spacing: StockSpacing
    @Composable get() = LocalSpacing.current

val MaterialTheme.radius: StockRadius
    @Composable get() = LocalRadius.current
