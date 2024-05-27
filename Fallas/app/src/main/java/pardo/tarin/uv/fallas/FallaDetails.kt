package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import pardo.tarin.uv.fallas.databinding.FragmentFallaDetailsBinding
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pardo.tarin.uv.fallas.bdRoom.AppDatabase

class FallaDetails : Fragment() {

    private lateinit var binding: FragmentFallaDetailsBinding
    private lateinit var falla: Falla
    private var tipo: String = ""
    private var imgBtnFav: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        falla = arguments?.getSerializable("falla") as Falla
        /*try {
            falla = arguments?.getSerializable("adultas") as Falla
            tipo = "adultas"
        }
        catch (e: Exception)
        {
            falla = arguments?.getSerializable("infantiles") as Falla
            tipo = "infantiles"
        }*/
        //falla = arguments?.getSerializable("falla") as Falla

        binding = FragmentFallaDetailsBinding.inflate(inflater, container, false)
        imgBtnFav = (activity as MainActivity).botonfav
        imgBtnFav!!.visibility = View.VISIBLE
        comprobarFavorito(falla) { esfavorito ->
            if (esfavorito) {
                imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }

        (activity as MainActivity).botonfav.setOnClickListener {
            comprobarFavorito(falla) { esfavorito ->
                if (esfavorito) {
                    imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_off)
                    falla.favorito = false
                    borrarFavorito(falla)
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_on)
                    falla.favorito = true
                    añadirFavorito(falla)
                    Toast.makeText(requireContext(), "Añadido a favoritos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.mapaButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Abrir Maps")
                .setMessage("¿Quieres abrir Google Maps para obtener la ruta?")
                .setPositiveButton("Sí") { _, _ ->
                    //val uri = Uri.parse("geo:0,0?q=${falla.coordenadas?.first},${falla.coordenadas?.second}(${falla.nombre})")
                    val uri = Uri.parse("geo:0,0?q=${falla.coordLat},${falla.coordLong}(${falla.nombre})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.boceto.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Abrir boceto")
                .setMessage("¿Quieres abrir el boceto en internet?")
                .setPositiveButton("Sí") { _, _ ->
                    val searchTerm = falla.boceto // reemplaza con el término de búsqueda que quieras
                    val uri = Uri.parse(searchTerm)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FallaDetails", "${falla.boceto}")
        val imageview = binding.boceto
        Glide.with(this)
            .load("${falla.boceto}")
            .into(imageview)
        /*Glide.with(this)
            .load("${falla.escudo}")
            .into(binding.escudo)*/
        binding.nombreBoceto.text = falla.lema
        binding.nombreArtista.text = "Artista: ${falla.artista}"
        /*if(tipo == "adultas")
        {
            binding.nombreFallera.text = "Fallera Mayor:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente:\n     ${falla.presidente}"
        }
        else
        {
            binding.nombreFallera.text = "Fallera Mayor Infantil:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente Infantil:\n     ${falla.presidente}"
        }*/
        if(falla.seccion == "IE" || falla.seccion == "E")
        {
            binding.seccionText.text = "SECCIÓN ESPECIAL"
        }
        else if(falla.seccion == "FC")
        {
            binding.seccionText.text = "SECCIÓN FUERA CONCURSO"
            binding.premios.visibility = View.GONE
        }
        else
            binding.seccionText.text = "SECCIÓN ${falla.seccion}"

        if(falla.premio == "Sin premio" && falla.premioE == "Sin premio")
        {
            binding.premios.visibility = View.GONE
        }
        else {
            if (falla.premio != "Sin premio") {
                binding.numeroPremio.text = "${falla.premio}º"
            } else {
                binding.PremioSeccion.visibility = View.GONE
            }

            if (falla.premioE != "Sin premio") {
                binding.numeroIE.text = "${falla.premioE}º"
            } else {
                binding.PremioIE.visibility = View.GONE
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = falla.nombre
    }

    fun añadirFavorito(falla: Falla) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()
        val falla = Falla(falla.objid, falla.id, falla.nombre, falla.escudo, falla.seccion, falla.premio, falla.premioE, falla.fallera, falla.presidente, falla.artista, falla.lema, falla.boceto, falla.experim, falla.coordLat, falla.coordLong)
        GlobalScope.launch {
            try{
                fallaDao.insertFalla(falla)
                Log.d("FavoritosFallaDetails", falla.favorito.toString())
                Log.d("FavoritosDetails", fallaDao.getAll().toString())
                Log.d("CampingDetails", "Camping ${falla.nombre} añadido a favoritos")
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al añadir el camping a favoritos: ${e.message}")
            }
        }
    }

    fun borrarFavorito(falla: Falla) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()
        GlobalScope.launch {
            try{
                fallaDao.delete(falla)
                Log.d("FavoritosFallaDetails", falla.favorito.toString())
                Log.d("FavoritosDetails", fallaDao.getAll().toString())
                Log.d("CampingDetails", "Camping ${falla.nombre} eliminado de favoritos")
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al eliminar el camping de favoritos: ${e.message}")
            }
        }
    }

    fun comprobarFavorito(falla: Falla, callback: (Boolean) -> Unit) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()
        GlobalScope.launch {
            try {
                val favorito = fallaDao.getFalla(falla.objid)
                withContext(Dispatchers.Main) {
                    callback(favorito != null)
                }
            } catch (e: Exception) {
                Log.e("FallaDetails", "Error al comprobar si la falla es favorita: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).botonfav.visibility = View.GONE
    }
}