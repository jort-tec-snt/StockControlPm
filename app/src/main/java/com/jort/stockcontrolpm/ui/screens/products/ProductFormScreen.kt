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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jort.stockcontrolpm.domain.model.PredefinedCategories
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    uiState: ProductFormUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onMinStockChange: (String) -> Unit,
    onUnitPriceChange: (String) -> Unit,
    onExpirationDateChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onClearError: () -> Unit,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onSkuChange: ((String) -> Unit)? = null,
    onSupplierChange: ((String) -> Unit)? = null,
    onPurchasePriceChange: ((String) -> Unit)? = null,
    onDescriptionChange: ((String) -> Unit)? = null,
    onShowCategorySheet: (() -> Unit)? = null,
    onDismissCategorySheet: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // BottomSheet de categorías
    if (uiState.showCategorySheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onDismissCategorySheet?.invoke() },
            sheetState       = sheetState,
            containerColor   = Surface
        ) {
            CategoryPickerSheet(
                selected     = uiState.category,
                onSelect     = onCategoryChange,
                onDismiss    = { onDismissCategorySheet?.invoke() }
            )
        }
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
                text       = if (uiState.isEditing) "Editar producto" else "Nuevo producto",
                style      = MaterialTheme.typography.headlineMedium,
                color      = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                modifier   = Modifier.weight(1f)
            )
        }

        // ── Formulario ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
        ) {
            // Error banner
            if (uiState.errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.radius.md))
                        .background(ErrorLight)
                        .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.md))
                        .padding(MaterialTheme.spacing.space3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(uiState.errorMessage, style = MaterialTheme.typography.bodySmall,
                        color = Error, modifier = Modifier.weight(1f))
                    IconButton(onClick = onClearError, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Outlined.Close, null, tint = Error, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // ─ Sección 1: Identificación ──────────────────────────────────────
            FormSection(title = "Identificación") {
                FormField(
                    value         = uiState.name,
                    onValueChange = onNameChange,
                    label         = "Nombre del producto *",
                    placeholder   = "Ej: Leche Gloria 1L",
                    imeAction     = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
                FormField(
                    value         = uiState.sku,
                    onValueChange = { onSkuChange?.invoke(it) },
                    label         = "SKU / Código",
                    placeholder   = "Ej: GLO-001",
                    imeAction     = ImeAction.Next
                )

                // Selector de categoría (toca el campo → abre BottomSheet)
                CategoryPickerField(
                    value   = uiState.category,
                    onClick = { onShowCategorySheet?.invoke() }
                )
            }

            // ─ Sección 2: Stock ───────────────────────────────────────────────
            FormSection(title = "Stock") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                ) {
                    FormField(
                        value         = uiState.stock,
                        onValueChange = onStockChange,
                        label         = "Stock actual *",
                        placeholder   = "0",
                        keyboardType  = KeyboardType.Number,
                        imeAction     = ImeAction.Next,
                        modifier      = Modifier.weight(1f)
                    )
                    FormField(
                        value         = uiState.minStock,
                        onValueChange = onMinStockChange,
                        label         = "Stock mínimo *",
                        placeholder   = "5",
                        keyboardType  = KeyboardType.Number,
                        imeAction     = ImeAction.Next,
                        modifier      = Modifier.weight(1f)
                    )
                }
                FormField(
                    value         = uiState.expirationDate,
                    onValueChange = onExpirationDateChange,
                    label         = "Fecha de vencimiento",
                    placeholder   = "AAAA-MM-DD",
                    imeAction     = ImeAction.Next
                )
            }

            // ─ Sección 3: Precios ─────────────────────────────────────────────
            FormSection(title = "Precios") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space3)
                ) {
                    FormField(
                        value         = uiState.unitPrice,
                        onValueChange = onUnitPriceChange,
                        label         = "Precio de venta *",
                        placeholder   = "0.00",
                        keyboardType  = KeyboardType.Decimal,
                        imeAction     = ImeAction.Next,
                        prefix        = "S/ ",
                        modifier      = Modifier.weight(1f)
                    )
                    FormField(
                        value         = uiState.purchasePrice,
                        onValueChange = { onPurchasePriceChange?.invoke(it) },
                        label         = "Precio de compra",
                        placeholder   = "0.00",
                        keyboardType  = KeyboardType.Decimal,
                        imeAction     = ImeAction.Next,
                        prefix        = "S/ ",
                        modifier      = Modifier.weight(1f)
                    )
                }
            }

            // ─ Sección 4: Proveedor y descripción ────────────────────────────
            FormSection(title = "Proveedor y descripción") {
                FormField(
                    value         = uiState.supplier,
                    onValueChange = { onSupplierChange?.invoke(it) },
                    label         = "Proveedor",
                    placeholder   = "Ej: Distribuidora Norte SAC",
                    imeAction     = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
                FormField(
                    value         = uiState.description,
                    onValueChange = { onDescriptionChange?.invoke(it) },
                    label         = "Descripción",
                    placeholder   = "Notas adicionales del producto…",
                    imeAction     = ImeAction.Done,
                    singleLine    = false,
                    minLines      = 3
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))

            // ─ Botón guardar ──────────────────────────────────────────────────
            Button(
                onClick  = onSaveClick,
                enabled  = !uiState.isLoading && !uiState.isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(MaterialTheme.radius.lg),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Primary,
                    contentColor           = Color.White,
                    disabledContainerColor = Primary.copy(alpha = 0.45f),
                    disabledContentColor   = Color.White.copy(alpha = 0.45f)
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.space2))
                    Text("Guardando…", fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = if (uiState.isEditing) "Guardar cambios" else "Registrar producto",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8))
        }
    }
}

// ── Sección con título ────────────────────────────────────────────────────────
@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.radius.xl))
            .background(Surface)
            .border(0.5.dp, Divider, RoundedCornerShape(MaterialTheme.radius.xl))
            .padding(MaterialTheme.spacing.space4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))
        content()
    }
}

// ── Campo de formulario reutilizable ──────────────────────────────────────────
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    prefix: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            placeholder   = { Text(placeholder, color = TextMuted) },
            prefix        = if (prefix != null) {{ Text(prefix, color = TextMuted) }} else null,
            singleLine    = singleLine,
            minLines      = minLines,
            keyboardOptions = KeyboardOptions(
                keyboardType  = keyboardType,
                imeAction     = imeAction,
                capitalization = capitalization
            ),
            shape  = RoundedCornerShape(MaterialTheme.radius.lg),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Primary,
                unfocusedBorderColor    = Divider,
                focusedContainerColor   = Bg,
                unfocusedContainerColor = Bg,
                cursorColor             = Primary
            )
        )
    }
}

// ── Selector de categoría como campo clicable ─────────────────────────────────
@Composable
private fun CategoryPickerField(value: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text  = "CATEGORÍA *",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                .border(1.dp, Divider, RoundedCornerShape(MaterialTheme.radius.lg))
                .background(Bg)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = MaterialTheme.spacing.space4,
                    vertical   = MaterialTheme.spacing.space4
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text  = value.ifBlank { "Seleccionar categoría…" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (value.isBlank()) TextMuted else TextPrimary
            )
            Icon(Icons.Outlined.ArrowDropDown, null, tint = TextMuted)
        }
    }
}

// ── BottomSheet de categorías ─────────────────────────────────────────────────
@Composable
private fun CategoryPickerSheet(
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start  = MaterialTheme.spacing.space5,
                end    = MaterialTheme.spacing.space5,
                bottom = MaterialTheme.spacing.space8,
                top    = MaterialTheme.spacing.space2
            ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Seleccionar categoría", style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Outlined.Close, "Cerrar", tint = TextMuted)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Divider))
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))

        PredefinedCategories.forEach { cat ->
            val isSelected = cat == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(MaterialTheme.radius.lg))
                    .background(if (isSelected) PrimaryLight else Color.Transparent)
                    .clickable { onSelect(cat) }
                    .padding(
                        horizontal = MaterialTheme.spacing.space4,
                        vertical   = MaterialTheme.spacing.space3
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = cat,
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = if (isSelected) Primary else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                if (isSelected) {
                    Icon(Icons.Outlined.Check, null, tint = Primary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
