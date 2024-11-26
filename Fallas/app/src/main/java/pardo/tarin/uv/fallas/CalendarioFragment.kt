package pardo.tarin.uv.fallas

import EventAdapter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import pardo.tarin.uv.fallas.databinding.FragmentCalendarioBinding
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class CalendarioFragment : Fragment() {

    private lateinit var binding: FragmentCalendarioBinding

    private var position = 0
    @RequiresApi(Build.VERSION_CODES.O)
    private val calendario = List(if (LocalDate.now().isLeapYear) 366 else 365) { LocalDate.ofYearDay(LocalDate.now().year, it + 1) }
    private val db = FirebaseFirestore.getInstance()
    private lateinit var eventAdapter: EventAdapter
    private var calendarioButton: ImageButton? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.home_calendario)
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        val monthTextView = binding.mes
        val calendar = binding.calendarView
        val viewCalendario = binding.viewCalendario
        val adapter = CalendarioAdapter(calendario)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        calendarioButton = (activity as MainActivity).botonfav
        calendarioButton!!.setImageResource(R.drawable.calendario32)

        calendarioButton!!.setOnClickListener() {

            viewCalendario.visibility = View.VISIBLE
        }

        val minDate = calendario.first()
        calendar.minDate = Date.from(minDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        val maxDate = calendario.last()
        calendar.maxDate = Date.from(maxDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).time

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val position = adapter.getPositionForDate(selectedDate)
            adapter.selectedItem = position

            // Scroll the RecyclerView to the selected date
            val itemWidth = recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.width ?: 0
            val offset = (recyclerView.width - itemWidth) / 2
            layoutManager.scrollToPositionWithOffset(position, offset)

            monthTextView.text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase(
                Locale.getDefault())

            viewCalendario.visibility = View.GONE
            calendar.date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()).time

            fetchEventsForDate(selectedDate)
        }

        val today = LocalDate.now()
        calendar.date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        position = today.dayOfYear - 1
        // Desplaza el RecyclerView a la posición de la fecha actual
        val day = calendario[position]
        adapter.selectedItem = position
        monthTextView.text = day.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase(
            Locale.getDefault())
        recyclerView.post {
            val itemWidth = recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.width ?: 0
            val offset = (recyclerView.width - itemWidth) / 2
            layoutManager.scrollToPositionWithOffset(position, offset)
        }
        fetchEventsForDate(today)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        eventAdapter = EventAdapter(this, emptyList())
        binding.eventosRV.adapter = eventAdapter
        binding.eventosRV.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    val pos = layoutManager.getPosition(centerView!!)
                    // Actualiza el elemento seleccionado
                    adapter.selectedItem = pos
                    val selectedDay = calendario[pos]
                    monthTextView.text =
                        selectedDay.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase(Locale.getDefault())
                    fetchEventsForDate(selectedDay)
                }
            }
        })



        return binding.root
    }
    // Función para obtener los eventos de Firestore según la fecha seleccionada
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchEventsForDate(date: LocalDate) {
        val startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

        db.collection("eventos")
            .whereGreaterThanOrEqualTo("Fecha", startOfDay)
            .whereLessThan("Fecha", endOfDay)
            .get()
            .addOnSuccessListener { documents ->
                val eventList = mutableListOf<CalendarioAdapter.Evento>()
                for (document in documents) {
                    val nombre = document.getString("Nombre")
                    val fecha = document.getDate("Fecha")
                    val lugar = document.getString("Localización")
                    val descripcion = document.getString("Descripción")
                    val descripcionEng = document.getString("DescripciónEng")
                    val event = CalendarioAdapter.Evento(document.id, nombre, fecha, lugar, descripcion, descripcionEng)
                    eventList.add(event)
                }

                if (eventList.isEmpty()) {
                    binding.eventosRV.visibility = View.GONE
                    binding.textNoEvents.visibility = View.VISIBLE
                } else {
                    binding.eventosRV.visibility = View.VISIBLE
                    binding.textNoEvents.visibility = View.GONE
                }
                Log.d("CalendarioFragment", "eventList: $eventList")

                // Actualiza el RecyclerView de eventos usando el método updateEvents
                eventAdapter.updateEvents(eventList)
            }
            .addOnFailureListener { exception ->
                Log.w("CalendarioFragment", "Error getting documents: ", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showEventDetails(event: CalendarioAdapter.Evento) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(event.nombre)
        val fechaText = SpannableStringBuilder(getString(R.string.fecha))
        val date = event.fecha!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        fechaText.setSpan(StyleSpan(Typeface.BOLD), 0, fechaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val formattedDate: String = if(Locale.getDefault().language == "es") {
            val daySpanish = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(
                Locale.getDefault())
            val monthSpanish = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).capitalize(Locale.getDefault())
            "${daySpanish}, ${date.dayOfMonth} de $monthSpanish de ${date.year} - ${date.format(
                DateTimeFormatter.ofPattern("HH:mm"))}h"
        } else {
            date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - h:mm a"))
        }
        val lugar = SpannableStringBuilder(getString(R.string.lugar))
        lugar.setSpan(StyleSpan(Typeface.BOLD), 0, lugar.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val descripcionText = SpannableStringBuilder(getString(R.string.descripcion))
        descripcionText.setSpan(StyleSpan(Typeface.BOLD), 0, descripcionText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val description : String = if (Locale.getDefault().language == "es") {
            event.descripcion!!
        } else {
            event.descripcionEng!!
        }


        builder.setMessage(
            TextUtils.concat(
            fechaText, ": ${formattedDate}\n\n",
            lugar, ": ${event.lugar}\n\n",
                descripcionText, ": $description"
        ))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        // Obtén una referencia a botonfav
        val botonfav = (activity as MainActivity).botonfav
        botonfav.visibility = View.VISIBLE
        val viewCalendario = binding.viewCalendario

        // Cambia la imagen de botonfav
        botonfav.setImageResource(R.drawable.calendario32)
        botonfav.setOnClickListener {
            viewCalendario.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()

        // Obtén una referencia a botonfav
        val botonfav = (activity as MainActivity).botonfav

        // Haz que botonfav desaparezca
        botonfav.visibility = View.GONE
    }
}