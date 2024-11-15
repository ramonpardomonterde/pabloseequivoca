import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.fallas.AlarmReceiver
import pardo.tarin.uv.fallas.CalendarioAdapter
import pardo.tarin.uv.fallas.CalendarioFragment
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.bdRoom.AppDatabase
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class EventAdapter(private val fragment: CalendarioFragment, private var events: List<CalendarioAdapter.Evento>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    val db = FirebaseFirestore.getInstance()
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.event_name)
        val fechaTextView: TextView = itemView.findViewById(R.id.event_date)
        val lugarTextView: TextView = itemView.findViewById(R.id.event_location)
        val btnSetAlarm: Button = itemView.findViewById(R.id.btn_set_alarm)
        val btnDeleteAlarm: Button = itemView.findViewById(R.id.btn_delete_alarm)

        init {
            itemView.findViewById<Button>(R.id.details_button).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val event = events[position]
                    fragment.showEventDetails(event)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_view, parent, false)
        return EventViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.nombreTextView.text = event.nombre
        val date = event.fecha!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formattedDate: String = if(Locale.getDefault().language == "es") {
            val daySpanish = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(
                Locale.getDefault())
            val monthSpanish = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(Locale.getDefault())
            "${daySpanish}, ${date.dayOfMonth} de $monthSpanish de ${date.year} - ${date.format(DateTimeFormatter.ofPattern("HH:mm"))}h"
        } else {
            date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - h:mm a"))
        }
        holder.fechaTextView.text = formattedDate
        holder.lugarTextView.text = event.lugar


        // Verificar si la alarma ya está programada usando un requestCode único basado en el evento
        /*val alarmManager = holder.itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(holder.itemView.context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            holder.itemView.context,
            event.nombre.hashCode(), // Usa un requestCode único basado en el evento
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        // Reiniciamos siempre la visibilidad de los botones
        holder.btnSetAlarm.visibility = View.VISIBLE
        holder.btnDeleteAlarm.visibility = View.GONE

        // Si ya existe un PendingIntent, mostrar el botón de eliminar alarma
        if (pendingIntent != null) {
            holder.btnSetAlarm.visibility = View.GONE
            holder.btnDeleteAlarm.visibility = View.VISIBLE
        }*/
        val db = Room.databaseBuilder(holder.itemView.context, AppDatabase::class.java, "eventos").fallbackToDestructiveMigration().build()
        val evDao = db.eventDao()
        GlobalScope.launch {
            val ev = evDao.getEvento(event.id)
            withContext(Dispatchers.Main) {
                if(ev != null) {
                    holder.btnSetAlarm.visibility = View.GONE
                    holder.btnDeleteAlarm.visibility = View.VISIBLE
                } else {
                    holder.btnSetAlarm.visibility = View.VISIBLE
                    holder.btnDeleteAlarm.visibility = View.GONE
                }
            }
        }

        val fechaActual = LocalDateTime.now()
        val fechaEvento = event.fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        if (fechaEvento.isBefore(fechaActual)) {
            // Si la fecha del evento es anterior a la fecha actual, deshabilitar el botón setAlarm
            holder.btnSetAlarm.isEnabled = false
            holder.btnDeleteAlarm.isEnabled = false
        } else {
            holder.btnSetAlarm.isEnabled = true
            holder.btnDeleteAlarm.isEnabled = true
            holder.btnSetAlarm.setOnClickListener {
                // Crear un NumberPicker
                val numberPicker = NumberPicker(it.context).apply {
                    minValue = 5 // valor mínimo
                    maxValue = 60 // valor máximo
                    value = 5 // valor por defecto
                }

                // Crear un AlertDialog
                val builder = AlertDialog.Builder(it.context)
                val title = if (Locale.getDefault().language == "es") "Selecciona los minutos de antelación" else "Select the minutes in advance"
                val aceptar = if (Locale.getDefault().language == "es") "Aceptar" else "Accept"
                val cancelar = if (Locale.getDefault().language == "es") "Cancelar" else "Cancel"
                builder.setTitle(title)
                builder.setView(numberPicker)
                builder.setPositiveButton(aceptar) { dialog, _ ->
                    dialog.dismiss()
                    // Obtener el valor seleccionado
                    val minutesBefore = numberPicker.value
                    // Llamar a setAlarm pasando los minutos seleccionados
                    setAlarm(holder.itemView.context, event, minutesBefore)
                    // Cambiar el botón una vez que la alarma ha sido programada
                    holder.btnSetAlarm.visibility = View.GONE
                    holder.btnDeleteAlarm.visibility = View.VISIBLE
                }
                builder.setNegativeButton(cancelar) { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }


            holder.btnDeleteAlarm.setOnClickListener {
                // Cancelar la alarma
                val builder = AlertDialog.Builder(it.context)
                val title = if (Locale.getDefault().language == "es") "Borrrar aviso" else "Delete notification"
                val message = if (Locale.getDefault().language == "es") "¿Quieres borrar el aviso para este evento?" else "Do you want to delete the advertisement for this event?"
                val si = if (Locale.getDefault().language == "es") "Sí" else "Yes"
                builder.setTitle(title)
                builder.setMessage(message)
                builder.setPositiveButton(si) { dialog, _ ->
                    dialog.dismiss()
                    cancelAlarm(holder.itemView.context, event)
                    // Cambiar el botón una vez que la alarma ha sido cancelada
                    holder.btnSetAlarm.visibility = View.VISIBLE
                    holder.btnDeleteAlarm.visibility = View.GONE
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    // Método para actualizar la lista de eventos
    fun updateEvents(newEvents: List<CalendarioAdapter.Evento>) {
        events = newEvents
        notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    // Método para programar la alarma
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setAlarm(context: Context, event: CalendarioAdapter.Evento, minutesBefore: Int) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (!notificationManager.areNotificationsEnabled()) {
            // Redirige al usuario a la configuración de notificaciones de la aplicación
            val intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

            // Para Android 5-7
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)

            // Para Android 8 y posteriores
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)

            context.startActivity(intent)
        } else {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            var message = ""
            message = if (Locale.getDefault().language == "es") {
                "¡${event.nombre} comienza en $minutesBefore minutos en ${event.lugar}!"
            } else {
                "${event.nombre} starts in $minutesBefore minutes at ${event.lugar}!"
            }
            intent.putExtra("event_name", event.nombre)
            intent.putExtra("message", message)

            // Crear el PendingIntent para la alarma
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Hora del evento menos 5 minutos
            val eventDateTime =
                event.fecha!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            val alarmDateTime = eventDateTime.minusMinutes(minutesBefore.toLong())

            // Convertir LocalDateTime a milisegundos
            val alarmTimeInMillis =
                alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Agregar un log para indicar el tiempo de la alarma
            Log.d("EventAdapter", "Intentando programar alarma para: $alarmDateTime")

            // Comprobar permiso antes de programar la alarma
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Log para indicar que no se puede programar la alarma
                    Log.w(
                        "EventAdapter",
                        "No se puede programar la alarma exacta: permiso no concedido"
                    )
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                    return // No continuar con la programación de la alarma
                }
            }

            // Programa la alarma exacta
            try {
                if (pendingIntent != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            alarmTimeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent)
                    }
                }

                // Log para confirmar que la alarma fue programada con éxito
                Log.d("EventAdapter", "Alarma programada con éxito para: $alarmDateTime")
                val db = Room.databaseBuilder(context, AppDatabase::class.java, "eventos").fallbackToDestructiveMigration().build()
                val evDao = db.eventDao()
                GlobalScope.launch {
                    evDao.insertEvento(event)
                }
                //db.collection("eventos").document(event.id.toString()).update("AlarmaCreada", true)
                val message =
                    if (Locale.getDefault().language == "es") "Aviso programado para $minutesBefore minutos antes del evento" else "Notice scheduled for $minutesBefore minutes before the event"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                // Log para indicar que ha fallado la programación de la alarma
                Log.e(
                    "EventAdapter",
                    "No se pudo programar la alarma exacta: permiso no otorgado",
                    e
                )
            } catch (e: Exception) {
                // Captura cualquier otro tipo de excepción y log
                Log.e("EventAdapter", "Error al programar la alarma: ${e.message}", e)
            }
        }
    }

    // Método para cancelar la alarma
    fun cancelAlarm(context: Context, event: CalendarioAdapter.Evento) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        //db.collection("eventos").document(event.id.toString()).update("AlarmaCreada", false)
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "eventos").fallbackToDestructiveMigration().build()
        val evDao = db.eventDao()
        GlobalScope.launch {
            evDao.delete(event)
        }
        Log.d("EventAdapter", "Alarma cancelada para el evento: ${event.nombre}")
        val msg = if(Locale.getDefault().language == "es") "Aviso eliminado" else "Notice removed"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
