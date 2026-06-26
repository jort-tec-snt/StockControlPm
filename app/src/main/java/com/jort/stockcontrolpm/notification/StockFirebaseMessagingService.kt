package com.jort.stockcontrolpm.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * StockFirebaseMessagingService
 *
 * Servicio que escucha mensajes de Firebase Cloud Messaging (FCM).
 * Debe estar registrado en AndroidManifest.xml con el intent-filter
 * "com.google.firebase.MESSAGING_EVENT".
 *
 * ¿Cómo funciona FCM?
 * → Cada dispositivo tiene un TOKEN único generado por FCM.
 * → El token identifica este dispositivo en el servidor de Firebase.
 * → Cuando el servidor envía un push, FCM lo entrega usando ese token.
 *
 * Dos tipos de mensajes FCM:
 * 1. Notification messages: FCM los muestra automáticamente cuando la app está en background.
 *    onMessageReceived() NO se llama si la app está cerrada.
 * 2. Data messages: siempre llaman onMessageReceived() (foreground y background).
 *    Tú eres responsable de mostrar la notificación.
 *
 * Esta implementación maneja el caso FOREGROUND (app abierta):
 * cuando llega un mensaje, construimos la notificación manualmente.
 */
class StockFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Se llama cuando llega un mensaje FCM y la app está en FOREGROUND.
     *
     * remoteMessage.notification: datos del mensaje (título, body) — tipo "Notification message"
     * remoteMessage.data: datos personalizados — tipo "Data message"
     *
     * En BACKGROUND: si el mensaje tiene "notification", FCM lo muestra solo (sin llamar aquí).
     *                Si el mensaje tiene solo "data", Android lo procesa en background
     *                y llama onMessageReceived() en un hilo separado.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extraer título y body del mensaje notification, con valores por defecto
        val title = remoteMessage.notification?.title ?: "StockControl"
        val body  = remoteMessage.notification?.body  ?: remoteMessage.data["message"] ?: "Nueva notificación"

        // Usar NotificationHelper para mostrar la notificación de forma consistente
        // El canal CHANNEL_STOCK_ALERTS ya fue creado en MainActivity.onCreate()
        NotificationHelper.showPushNotification(
            context = applicationContext,
            title   = title,
            body    = body
        )
    }

    /**
     * Se llama cuando FCM genera o renueva el token de este dispositivo.
     *
     * El token puede renovarse cuando:
     * → El usuario reinstala la app
     * → El usuario borra los datos de la app
     * → FCM lo expira automáticamente
     *
     * En producción: enviarías este nuevo token a tu servidor para actualizar
     * el registro y seguir pudiendo enviarle pushes.
     * Por ahora solo lo logueamos.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // En una app real: guardar token en Firestore bajo el UID del usuario
        // Para la sustentación: mostrar que recibiste el token nuevo
        android.util.Log.d("FCM", "Token renovado: $token")
    }
}
