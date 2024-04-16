package pardo.tarin.uv.fallas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        falla = arguments?.getSerializable("falla") as Falla

        binding = FragmentFallaDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun downloadImage(url: String): Bitmap? {
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                return bitmap
            } else {
                Log.e("DownloadImage", "Failed to download image: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("DownloadImage", "Error: ${e.message}")
        }

        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FallaDetails", "${falla.boceto}")
        val imageview = binding.boceto
        binding.progressBarBoceto.visibility = View.VISIBLE
        Glide.with(this)
            .load("${falla.boceto}")
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Aquí puedes manejar el caso de que la carga de la imagen falle
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // Aquí puedes manejar el caso de que la imagen se haya cargado correctamente
                    // Por ejemplo, puedes ocultar la barra de progreso aquí
                    binding.progressBarBoceto.visibility = View.GONE
                    return false
                }
            })
            .into(imageview)
        binding.progressBarBoceto.visibility = View.GONE
        binding.nombreBoceto.text = falla.lema
        binding.nombreFallera.text = falla.fallera
        binding.nombrePresidente.text = falla.presidente
        if(falla.seccion == "IE" || falla.seccion == "E")
        {
            binding.seccionText.text = "SECCIÓN ESPECIAL"
        }
        else
            binding.seccionText.text = "SECCIÓN ${falla.seccion}"
        if(falla.premio != "0")
        {
            binding.numeroPremio.text = "${falla.premio}º"
        }

        if(falla.premioE != 0)
        {
            binding.numeroIE.text = "${falla.premioE}º"
        }
        (activity as AppCompatActivity).supportActionBar?.title = falla.nombre
    }
}