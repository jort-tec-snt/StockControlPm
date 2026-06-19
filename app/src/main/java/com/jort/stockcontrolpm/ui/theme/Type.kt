package com.jort.stockcontrolpm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Fuente principal: Inter ────────────────────────────────────────────────
// Si Inter no está como recurso, Compose usa el sistema (Roboto).
// Para registrar Inter: agregar archivos .ttf en res/font/ y referenciarlos aquí.
val InterFamily = FontFamily.Default // reemplazar por FontFamily(Font(...)) cuando se agregue Inter

// ── Escala tipográfica STOCKCONTROL ───────────────────────────────────────
val Typography = Typography(

    // Display — valores KPI grandes (22sp / 800)
    displayMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),

    // H1 — título de pantalla (20sp / 800)
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),

    // H2 — sección, nombre de producto (17sp / 700)
    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),

    // H3 — nombre en lista (15sp / 700)
    headlineSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),

    // Body — descripciones, párrafos (14sp / 400)
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // Caption — metadata, fechas (12sp / 500)
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Label — etiquetas de campo ALL CAPS (11sp / 700)
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),

    // Mono — SKU, códigos, precios en tabla (12sp / 400)
    // Se usa FontFamily.Monospace donde aplique
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
