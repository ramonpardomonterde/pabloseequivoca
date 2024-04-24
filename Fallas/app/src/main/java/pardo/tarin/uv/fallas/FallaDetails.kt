package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import pardo.tarin.uv.fallas.databinding.FragmentFallaDetailsBinding

class FallaDetails : Fragment() {

    private lateinit var binding: FragmentFallaDetailsBinding
    private lateinit var falla: Falla
    private var tipo: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            falla = arguments?.getSerializable("adultas") as Falla
            tipo = "adultas"
        }
        catch (e: Exception)
        {
            falla = arguments?.getSerializable("infantiles") as Falla
            tipo = "infantiles"
        }
        //falla = arguments?.getSerializable("falla") as Falla

        binding = FragmentFallaDetailsBinding.inflate(inflater, container, false)

        binding.mapaButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Abrir Maps")
                .setMessage("¿Quieres abrir Google Maps para obtener la ruta?")
                .setPositiveButton("Sí") { _, _ ->
                    val uri = Uri.parse("geo:0,0?q=${falla.coordenadas?.first},${falla.coordenadas?.second}(${falla.nombre})")
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
        Glide.with(this)
            .load("${falla.escudo}")
            .into(binding.escudo)
        binding.nombreBoceto.text = falla.lema
        binding.nombreArtista.text = "Artista: ${falla.artista}"
        if(tipo == "adultas")
        {
            binding.nombreFallera.text = "Fallera Mayor:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente:\n     ${falla.presidente}"
        }
        else
        {
            binding.nombreFallera.text = "Fallera Mayor Infantil:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente Infantil:\n     ${falla.presidente}"
        }
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
}