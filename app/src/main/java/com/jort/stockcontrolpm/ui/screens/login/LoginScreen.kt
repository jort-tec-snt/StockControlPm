package com.jort.stockcontrolpm.ui.screens.login

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
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) onLoginSuccess()
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header compacto (fondo primary) ──────────────────────────────────
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
            Icon(
                imageVector        = Icons.Outlined.Inventory2,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(32.dp)
            )
            Text(
                text       = "STOCKCONTROL",
                style      = MaterialTheme.typography.headlineLarge,
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text  = "Supervisión operativa para minimarkets",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.80f)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))
            Text(
                text       = "Iniciar sesión",
                style      = MaterialTheme.typography.headlineSmall,
                color      = Color.White.copy(alpha = 0.65f),
                fontWeight = FontWeight.Normal
            )
        }

        // ── Formulario ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.space5),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space4)
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))

            // Selector de rol
            Text(
                text       = "ACCEDER COMO",
                style      = MaterialTheme.typography.labelSmall,
                color      = TextSecondary
            )
            RoleSegmentedControl(
                selected  = uiState.role,
                onSelect  = onRoleChange
            )

            // Separador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Divider)
            )

            // Campo email
            LoginField(
                value         = uiState.email,
                onValueChange = onEmailChange,
                label         = "Correo electrónico",
                placeholder   = "admin@minimarket.pe",
                leadingIcon   = { Icon(Icons.Outlined.Mail, null, tint = TextMuted) },
                keyboardType  = KeyboardType.Email,
                imeAction     = ImeAction.Next,
                isError       = uiState.errorMessage != null && uiState.email.isBlank()
            )

            // Campo contraseña
            LoginField(
                value         = uiState.password,
                onValueChange = onPasswordChange,
                label         = "Contraseña",
                placeholder   = "••••••••",
                leadingIcon   = { Icon(Icons.Outlined.Lock, null, tint = TextMuted) },
                trailingIcon  = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.passwordVisible)
                                Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = "Mostrar contraseña",
                            tint = TextMuted
                        )
                    }
                },
                visualTransformation = if (uiState.passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType  = KeyboardType.Password,
                imeAction     = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { onLoginClick() }),
                isError       = uiState.errorMessage != null && uiState.password.length < 4
            )

            // Banner de error
            if (uiState.errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(MaterialTheme.radius.md))
                        .background(ErrorLight)
                        .border(1.dp, Error.copy(alpha = 0.25f), RoundedCornerShape(MaterialTheme.radius.md))
                        .padding(MaterialTheme.spacing.space3),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.space2)
                ) {
                    Text(
                        text  = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Error
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space2))

            // Botón principal
            Button(
                onClick  = onLoginClick,
                enabled  = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(MaterialTheme.radius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor   = Color.White,
                    disabledContainerColor = Primary.copy(alpha = 0.45f),
                    disabledContentColor   = Color.White.copy(alpha = 0.45f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color     = Color.White,
                        modifier  = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.size(MaterialTheme.spacing.space2))
                    Text("Verificando…", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                } else {
                    Text("Ingresar al sistema  →", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // Nota de acceso
            Text(
                text      = "El acceso es gestionado por el administrador del minimarket.",
                style     = MaterialTheme.typography.bodySmall,
                color     = TextMuted,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
            Text(
                text      = "Solicitar acceso",
                style     = MaterialTheme.typography.bodySmall,
                color     = Primary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier  = Modifier
                    .fillMaxWidth()
                    .clickable { /* v2.5: enlace a formulario de solicitud */ }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.space8))
        }
    }
}

// ── Selector de rol (SegmentedControl) ───────────────────────────────────────
@Composable
private fun RoleSegmentedControl(
    selected: UserRole,
    onSelect: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, Divider, RoundedCornerShape(12.dp))
            .background(androidx.compose.ui.graphics.Color.Transparent)
    ) {
        UserRole.entries.forEach { role ->
            val isSelected = role == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .background(
                        if (isSelected) Primary else Color.Transparent,
                        shape = when (role) {
                            UserRole.OWNER   -> RoundedCornerShape(topStart = 11.dp, bottomStart = 11.dp)
                            UserRole.CASHIER -> RoundedCornerShape(topEnd = 11.dp, bottomEnd = 11.dp)
                        }
                    )
                    .clickable { onSelect(role) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = role.label,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color      = if (isSelected) Color.White else TextSecondary
                )
            }
        }
    }
}

// ── Campo de texto reutilizable para el login ─────────────────────────────────
@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false
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
            leadingIcon   = leadingIcon,
            trailingIcon  = trailingIcon,
            isError       = isError,
            singleLine    = true,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction    = imeAction
            ),
            keyboardActions = keyboardActions,
            shape  = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Primary,
                unfocusedBorderColor = Divider,
                errorBorderColor     = Error,
                focusedContainerColor   = Surface,
                unfocusedContainerColor = Surface,
                errorContainerColor     = ErrorLight,
                cursorColor          = Primary
            )
        )
    }
}
