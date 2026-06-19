package com.jort.stockcontrolpm.ui.screens.register

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun RegisterScreen(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) onRegisterSuccess()
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(
                    start  = MaterialTheme.spacing.space5,
                    end    = MaterialTheme.spacing.space5,
                    top    = MaterialTheme.spacing.space12,
                    bottom = MaterialTheme.spacing.space6
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
        ) {
            IconButton(
                onClick  = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Outlined.ArrowBack, "Volver", tint = Color.White)
            }
            Spacer(Modifier.height(MaterialTheme.spacing.space1))
            Icon(
                imageVector        = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(28.dp)
            )
            Text(
                text       = "Crear cuenta",
                style      = MaterialTheme.typography.headlineLarge,
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text  = "Completa tus datos para registrarte",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.80f)
            )
        }

        // ── Formulario ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Bg)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
        ) {
            Spacer(Modifier.height(MaterialTheme.spacing.space1))

            // Nombre completo
            RegField(
                value         = uiState.name,
                onValueChange = onNameChange,
                label         = "Nombre completo",
                placeholder   = "Ej. Juan Pérez",
                icon          = Icons.Outlined.Person,
                isError       = uiState.name.isNotBlank() && uiState.nameError != null,
                supportText   = if (uiState.name.isNotBlank()) uiState.nameError else null,
                imeAction     = ImeAction.Next
            )

            // Correo
            RegField(
                value         = uiState.email,
                onValueChange = onEmailChange,
                label         = "Correo electrónico",
                placeholder   = "correo@ejemplo.com",
                icon          = Icons.Outlined.Mail,
                keyboardType  = KeyboardType.Email,
                isError       = uiState.email.isNotBlank() && uiState.emailError != null,
                supportText   = if (uiState.email.isNotBlank()) uiState.emailError else null,
                imeAction     = ImeAction.Next
            )

            // Contraseña
            RegField(
                value         = uiState.password,
                onValueChange = onPasswordChange,
                label         = "Contraseña",
                placeholder   = "Mínimo 6 caracteres",
                icon          = Icons.Outlined.Lock,
                isError       = uiState.password.isNotBlank() && uiState.passwordError != null,
                supportText   = if (uiState.password.isNotBlank()) uiState.passwordError else null,
                keyboardType  = KeyboardType.Password,
                imeAction     = ImeAction.Next,
                visualTransformation = if (uiState.passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.passwordVisible)
                                Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null, tint = TextMuted
                        )
                    }
                }
            )

            // Confirmar contraseña
            RegField(
                value         = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label         = "Confirmar contraseña",
                placeholder   = "Repite tu contraseña",
                icon          = Icons.Outlined.Lock,
                isError       = uiState.confirmPassword.isNotBlank() && uiState.confirmError != null,
                supportText   = if (uiState.confirmPassword.isNotBlank()) uiState.confirmError else null,
                keyboardType  = KeyboardType.Password,
                imeAction     = ImeAction.Done,
                visualTransformation = if (uiState.passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardActions = KeyboardActions(onDone = { onRegisterClick() })
            )

            // Selector de rol
            Text(
                text  = "ROL EN EL SISTEMA",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, Divider, RoundedCornerShape(12.dp)),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("OWNER" to "Propietario", "CASHIER" to "Cajero").forEach { (value, label) ->
                    val selected = uiState.role == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                if (selected) Primary else Color.Transparent,
                                shape = when (value) {
                                    "OWNER"   -> RoundedCornerShape(topStart = 11.dp, bottomStart = 11.dp)
                                    else      -> RoundedCornerShape(topEnd = 11.dp, bottomEnd = 11.dp)
                                }
                            )
                            .clickable { onRoleChange(value) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Badge, null,
                                tint     = if (selected) Color.White else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text       = label,
                                style      = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Banner de error global
            if (uiState.errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.radius.md))
                        .background(ErrorLight)
                        .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.md))
                        .padding(MaterialTheme.spacing.space3)
                ) {
                    Text(
                        text  = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Error
                    )
                }
            }

            Spacer(Modifier.height(MaterialTheme.spacing.space1))

            // Botón registrar
            Button(
                onClick  = onRegisterClick,
                enabled  = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(MaterialTheme.radius.lg),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Primary,
                    contentColor           = Color.White,
                    disabledContainerColor = Primary.copy(alpha = 0.45f),
                    disabledContentColor   = Color.White.copy(alpha = 0.45f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Crear cuenta  →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // Link volver al login
            Text(
                text      = "¿Ya tienes cuenta? Inicia sesión",
                style     = MaterialTheme.typography.bodySmall,
                color     = Primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth().clickable { onBackClick() }
            )

            Spacer(Modifier.height(MaterialTheme.spacing.space8))
        }
    }
}

// ── Campo reutilizable ────────────────────────────────────────────────────────
@Composable
private fun RegField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            leadingIcon   = { Icon(icon, null, tint = TextMuted) },
            trailingIcon  = trailingIcon,
            isError       = isError,
            singleLine    = singleLine,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = keyboardActions,
            shape  = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Primary,
                unfocusedBorderColor    = Divider,
                errorBorderColor        = Error,
                focusedContainerColor   = Surface,
                unfocusedContainerColor = Surface,
                errorContainerColor     = ErrorLight,
                cursorColor             = Primary
            )
        )
        if (isError && supportText != null) {
            Text(supportText, style = MaterialTheme.typography.labelSmall, color = Error)
        }
    }
}
