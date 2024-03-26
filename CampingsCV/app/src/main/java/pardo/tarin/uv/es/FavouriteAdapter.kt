package pardo.tarin.uv.es

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pardo.tarin.uv.es.databinding.CampingViewBinding


class FavouriteAdapter(val campings: ArrayList<Camping>, private val listener: CampingFavoriteItemListener) : RecyclerView.Adapter<FavouriteAdapter.MyFavouriteViewHolder>() {

    class MyFavouriteViewHolder(val binding: CampingViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavouriteViewHolder {
        return MyFavouriteViewHolder(
            CampingViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyFavouriteViewHolder, position: Int) {
        holder.binding.apply {
            textViewNombre.text = campings[position].nombre
            textViewDireccion.text = "${campings[position].direccion}, ${campings[position].municipio}"
            //municipio.text = campings[position].municipio
            //ratingBar.rating = campings[position].categoria.toFloatOrNull() ?: 0.0f
            // Verificar si la categoría es null
            if (!campings[position].categoria[0].isDigit()) {
                // Si la categoría es null, hacer invisible el RatingBar y visible el TextView
                ratingBar.visibility = View.INVISIBLE
                //catPernoctar.visibility = View.VISIBLE
                //catPernoctar.text = "A pernoctar"
            } else {
                // Si la categoría no es null, hacer visible el RatingBar y invisible el TextView
                ratingBar.visibility = View.VISIBLE
                //catPernoctar.visibility = View.INVISIBLE
                ratingBar.rating = campings[position].categoria.toFloatOrNull() ?: 0.0f
            }
        }
        holder.itemView.setOnClickListener{
            listener.onItemClick(campings[position])
        }
    }

    override fun getItemCount(): Int = campings.size

    interface CampingFavoriteItemListener {
        fun onItemClick(camping: Camping)
    }

    fun actualizarDatos(nuevosCampings: List<Camping>) {
        campings.clear()
        campings.addAll(nuevosCampings)
        notifyDataSetChanged()
    }
}