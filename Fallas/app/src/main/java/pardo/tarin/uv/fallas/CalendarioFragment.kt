package pardo.tarin.uv.fallas

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import pardo.tarin.uv.fallas.databinding.FragmentCalendarioBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import com.google.firebase.database.*

class CalendarioFragment : Fragment() {

    private lateinit var binding: FragmentCalendarioBinding
    private var position = 0
    @RequiresApi(Build.VERSION_CODES.O)
    private val calendario = List(366) { LocalDate.ofYearDay(2024, it + 1) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarioBinding.inflate(inflater, container, false)

        val recyclerView = binding.recyclerView
        val monthTextView = binding.mes
        val adapter = CalendarioAdapter(calendario)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val today = LocalDate.now()
        Log.d("CalendarioFragment", "today: $today")
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

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    val pos = layoutManager.getPosition(centerView!!)
                    // Actualiza el elemento seleccionado
                    adapter.selectedItem = pos
                    val selectedDay = calendario[pos]
                    Log.d("CalendarioFragment", "selectedDay: $selectedDay")
                    monthTextView.text =
                        selectedDay.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase(Locale.getDefault())
                    //recyclerView.smoothScrollToPosition(selectedDay.dayOfYear - 1)
                    adapter.notifyDataSetChanged()
                }
            }
        })

        return binding.root
    }
}