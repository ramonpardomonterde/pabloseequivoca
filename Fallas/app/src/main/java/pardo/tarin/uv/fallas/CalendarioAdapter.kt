package pardo.tarin.uv.fallas

import android.os.Build
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pardo.tarin.uv.fallas.bdRoom.Converters
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

class CalendarioAdapter(private val calendario: List<LocalDate>) : RecyclerView.Adapter<CalendarioAdapter.CalendarioViewHolder>() {

    @Entity(tableName = "eventos")
    @TypeConverters(Converters::class)
    data class Evento(
        @PrimaryKey @ColumnInfo(name = "id_e") val id: String,
        val nombre: String? = null,
        val fecha: Date? = null,
        val lugar: String? = null,
    )
    var selectedItem = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    class CalendarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val dayOfWeek: TextView = itemView.findViewById(R.id.dayOfWeek)
        val selector: View = itemView.findViewById(R.id.selector)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dia, parent, false)
        return CalendarioViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarioViewHolder, position: Int) {
        val dia = calendario[position]
        val diasemana = dia.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase(Locale.getDefault())
        val dayOfMonthPadded = dia.dayOfMonth.toString().padStart(2, '0')
        //val monthName = dia.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase(Locale.getDefault())
        holder.dayTextView.text = dayOfMonthPadded
        holder.dayOfWeek.text = diasemana
        holder.selector.visibility = if (position == selectedItem) View.VISIBLE else View.GONE
        /*if (position == selectedItem) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FF6200EE")) // Cambia el color de fondo del elemento seleccionado
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFFFF")) // Restaura el color de fondo del elemento no seleccionado
        }*/

        // Ajusta el ancho del elemento para que solo se vean 7 elementos a la vez
        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val recyclerViewWidth = displayMetrics.widthPixels
        val itemWidth = recyclerViewWidth / 7
        val layoutParams = holder.itemView.layoutParams
        layoutParams.width = itemWidth
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount() = calendario.size
}