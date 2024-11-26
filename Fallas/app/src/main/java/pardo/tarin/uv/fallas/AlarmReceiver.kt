package pardo.tarin.uv.fallas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Obtener los datos del evento
        Log.d("EventAdapter", "Alarma recibida")
        val eventName = intent.getStringExtra("event_name") ?: "Evento"
        val message = intent.getStringExtra("message") ?: "Recordatorio de evento"

        // Mostrar la notificación
        showNotification(context, eventName, message)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificaciones
        val channelId = "event_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Eventos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para recordar eventos programados"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app cuando se toca la notificación
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Crear la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_app32)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Acción al tocar la notificación
            .setAutoCancel(true) // La notificación desaparece al tocarla
            .build()


        Log.d("EventAdapter", "Mostrando notificación: $title - $message")

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }
}