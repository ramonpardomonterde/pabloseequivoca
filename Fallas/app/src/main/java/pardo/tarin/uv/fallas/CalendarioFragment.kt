package pardo.tarin.uv.fallas

import EventAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import pardo.tarin.uv.fallas.databinding.FragmentCalendarioBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.util.Date

class CalendarioFragment : Fragment() {

    private lateinit var binding: FragmentCalendarioBinding
    private var position = 0
    @RequiresApi(Build.VERSION_CODES.O)
    private val calendario = List(366) { LocalDate.ofYearDay(2024, it + 1) }
    private val db = FirebaseFirestore.getInstance()
    // Mantén una sola instancia de EventAdapter
    private lateinit var eventAdapter: EventAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.home_calendario)
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        val monthTextView = binding.mes
        val adapter = CalendarioAdapter(calendario)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val today = LocalDate.now()
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

        eventAdapter = EventAdapter(emptyList())
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
                    //recyclerView.smoothScrollToPosition(selectedDay.dayOfYear - 1)
                    //adapter.notifyDataSetChanged()
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
                    val estado = document.getBoolean("Estado")
                    val alarmaCreada = document.getBoolean("AlarmaCreada")
                    val id = document.id
                    val event = CalendarioAdapter.Evento(nombre, fecha, lugar, estado, id, alarmaCreada)
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
}