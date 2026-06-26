package com.jort.stockcontrolpm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jort.stockcontrolpm.data.local.DatabaseSeeder
import com.jort.stockcontrolpm.data.local.database.AppDatabase
import com.jort.stockcontrolpm.data.remote.client.RetrofitClient
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.MovementRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.data.repository.UserRepository
import com.jort.stockcontrolpm.data.repository.VentaRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.jort.stockcontrolpm.notification.NotificationHelper
import com.jort.stockcontrolpm.ui.navigation.AppNavigation
import com.jort.stockcontrolpm.ui.theme.StockControlPmTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Launcher para solicitar el permiso POST_NOTIFICATIONS en Android 13+
    // registerForActivityResult debe llamarse antes de onCreate
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // granted = true: usuario aceptó; false: rechazó
            // La app funciona de todas formas — las notificaciones son opcionales
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Token FCM ─────────────────────────────────────────────────────────
        // Obtiene el token actual del dispositivo para recibir push notifications.
        // Cópialo de Logcat (tag "FCM_TOKEN") para enviarlo desde Firebase Console.
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                android.util.Log.d("FCM_TOKEN", "Token: ${task.result}")
            } else {
                android.util.Log.e("FCM_TOKEN", "Error obteniendo token", task.exception)
            }
        }

        // ── Canal de notificaciones ───────────────────────────────────────────
        // Debe crearse antes de intentar mostrar cualquier notificación.
        // Es idempotente: no crea duplicados si ya existe el canal.
        NotificationHelper.createNotificationChannel(this)

        // ── Permiso POST_NOTIFICATIONS (Android 13+ / API 33+) ───────────────
        // En versiones anteriores el permiso se concede automáticamente.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val alreadyGranted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!alreadyGranted) {
                // Muestra el diálogo del sistema pidiendo permiso al usuario
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val database = AppDatabase.getInstance(this)
        val productRepository  = ProductRepository(database.productDao())
        val movementRepository = MovementRepository(database.movementDao())
        val apiInfoRepository  = ApiInfoRepository(RetrofitClient.fakeStoreApiService)
        val userRepository     = UserRepository(database.userDao())
        val ventaRepository    = VentaRepository(database.ventaDao())

        // Carga productos desde la API si la base de datos está vacía (con imágenes reales)
        lifecycleScope.launch {
            DatabaseSeeder.seedIfEmpty(productRepository, apiInfoRepository)
        }

        setContent {
            StockControlPmTheme {
                AppNavigation(
                    productRepository  = productRepository,
                    movementRepository = movementRepository,
                    apiInfoRepository  = apiInfoRepository,
                    userRepository     = userRepository
                )
            }
        }
    }
}
