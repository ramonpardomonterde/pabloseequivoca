import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.es.Camping
import pardo.tarin.uv.es.base_datos.AppDatabase
import pardo.tarin.uv.es.databinding.CampingViewBinding

class MyAdapter(val campings: ArrayList<Camping>, private val listener: CampingItemListener) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: CampingViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(camping: Camping){
            binding.apply {
                textViewNombre.text = camping.nombre
                textViewDireccion.text = "${camping.direccion}, ${camping.municipio}"
                if(camping.categoria.toIntOrNull() == null){
                    apernoctar.text = "A PERNOCTAR"
                    ratingBar.visibility = View.GONE
                    apernoctar.visibility = View.VISIBLE
                } else {
                    ratingBar.rating = camping.categoria.toFloatOrNull() ?: 0.0f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            CampingViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = campings.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val camping = campings[position]
        holder.bind(camping)

        // Comprobar si el camping es un favorito
        GlobalScope.launch {
            val db = Room.databaseBuilder(
                holder.itemView.context,
                AppDatabase::class.java, "campings_database"
            ).build()
            val campingDao = db.campingDao()
            val isFavourite = campingDao.getCamping(camping.id) != null

            withContext(Dispatchers.Main) {
                if (isFavourite) {
                    holder.binding.heartIcon.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    holder.binding.heartIcon.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
        holder.binding.heartIcon.setOnClickListener {
            GlobalScope.launch {
                val db = Room.databaseBuilder(
                    holder.itemView.context,
                    AppDatabase::class.java, "campings_database"
                ).build()
                val campingDao = db.campingDao()
                val isFavourite = campingDao.getCamping(camping.id) != null

                if (isFavourite) {
                    campingDao.delete(camping)
                } else {
                    campingDao.insertCamping(camping)
                }

                withContext(Dispatchers.Main) {
                    if (isFavourite) {
                        Toast.makeText(
                            holder.itemView.context,
                            "'${camping.nombre}' eliminado de favoritos",
                            Toast.LENGTH_SHORT
                        ).show()
                        holder.binding.heartIcon.setImageResource(android.R.drawable.btn_star_big_off)
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            "'${camping.nombre}' añadido a favoritos",
                            Toast.LENGTH_SHORT
                        ).show()
                        holder.binding.heartIcon.setImageResource(android.R.drawable.btn_star_big_on)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener{
            listener.onItemClick(campings[position])
        }
    }

    interface CampingItemListener {
        fun onItemClick(camping: Camping)
    }

    fun actualizarDatos(nuevosCampings: List<Camping>) {
        campings.clear()
        campings.addAll(nuevosCampings)
        notifyDataSetChanged()
    }
}