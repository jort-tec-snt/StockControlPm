package com.jort.stockcontrolpm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jort.stockcontrolpm.MainActivity

/**
 * NotificationHelper
 *
 * Centraliza la creación y envío de notificaciones locales.
 *
 * ¿Por qué un NotificationChannel?
 * → Android 8.0 (API 26) introdujo los canales de notificación.
 *   El usuario puede silenciar o cambiar la importancia de cada canal en Ajustes.
 *   Las apps DEBEN declarar al menos un canal para mostrar notificaciones en API 26+.
 *
 * Flujo de una notificación local:
 * 1. Crear el canal (solo necesario una vez, idealmente en Application.onCreate)
 * 2. Construir la notificación con NotificationCompat.Builder
 * 3. Mostrarla con NotificationManagerCompat.notify()
 */
object NotificationHelper {

    // ID del canal: cadena única que identifica el canal de alertas de stock
    const val CHANNEL_STOCK_ALERTS = "channel_stock_alerts"

    // ID numérico de la notificación de stock bajo
    // Si envías dos notificaciones con el mismo ID, la segunda reemplaza a la primera
    private const val NOTIF_ID_LOW_STOCK = 1001

    /**
     * Crea el canal de notificación "Alertas de Stock".
     *
     * Debe llamarse en MainActivity.onCreate() o en la clase Application.
     * Es idempotente: llamarlo varias veces no crea canales duplicados.
     *
     * IMPORTANCE_HIGH: aparece como heads-up (popup en la parte superior de la pantalla).
     * IMPORTANCE_DEFAULT: solo aparece en la bandeja, sin popup.
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_STOCK_ALERTS,
            "Alertas de Inventario",     // Nombre visible en Ajustes del sistema
            NotificationManager.IMPORTANCE_HIGH  // Aparece como heads-up
        ).apply {
            description = "Notificaciones cuando el stock de un artículo es crítico o se agota"
        }

        // getSystemService: obtiene el servicio del sistema que gestiona notificaciones
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    /**
     * Muestra una notificación de stock bajo.
     *
     * @param context      Contexto Android para construir la notificación
     * @param productName  Nombre del producto con stock crítico
     * @param currentStock Stock actual del producto
     * @param minStock     Stock mínimo configurado
     */
    fun showLowStockNotification(
        context: Context,
        productName: String,
        currentStock: Int,
        minStock: Int
    ) {
        // PendingIntent: intención diferida que se ejecuta cuando el usuario toca la notificación.
        // Abre MainActivity, que luego navega al Dashboard/Alertas.
        val intent = Intent(context, MainActivity::class.java).apply {
            // Flags: si la app ya está abierta, la trae al frente en vez de crear nueva instancia
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            // FLAG_IMMUTABLE: requerido en Android 12+ por seguridad
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // NotificationCompat.Builder: construye la notificación de forma compatible con versiones antiguas
        val notification = NotificationCompat.Builder(context, CHANNEL_STOCK_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)  // Ícono en la barra de estado
            .setContentTitle("Stock crítico")
            .setContentText("$productName: $currentStock unidades (mínimo: $minStock)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)     // Para API < 26
            .setContentIntent(pendingIntent)                   // Acción al tocar la notificación
            .setAutoCancel(true)                               // Se descarta al tocarla
            .build()

        // notify(): muestra la notificación. Lanza SecurityException si falta el permiso
        // en Android 13+ y no fue concedido por el usuario.
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIF_ID_LOW_STOCK, notification)
        }
    }

    /**
     * Muestra una notificación genérica (para mensajes FCM recibidos en foreground).
     *
     * @param title   Título del push
     * @param body    Cuerpo del push
     */
    fun showPushNotification(context: Context, title: String, body: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_STOCK_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            // ID único basado en tiempo: evita que un push reemplace a otro
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}
