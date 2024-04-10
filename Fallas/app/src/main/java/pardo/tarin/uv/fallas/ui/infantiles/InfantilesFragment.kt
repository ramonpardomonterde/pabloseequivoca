package pardo.tarin.uv.fallas.ui.infantiles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import pardo.tarin.uv.fallas.Falla
import pardo.tarin.uv.fallas.databinding.FragmentInfantilesBinding
import java.io.IOException

class InfantilesFragment : Fragment() {

    private var _binding: FragmentInfantilesBinding? = null
    private lateinit var originalFallasData: ArrayList<Falla>
    private var campingsData: ArrayList<Falla> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val infantilesViewModel =
            ViewModelProvider(this).get(InfantilesViewModel::class.java)*/

        _binding = FragmentInfantilesBinding.inflate(inflater, container, false)

        originalFallasData = getFallasInfantiles()

        /*val textView: TextView = binding.textGallery
        textView.text = "This is infantiles Fragment"*/
        return binding.root
    }

    fun getFallasInfantiles(): ArrayList<Falla> {
        val url = "https://mural.uv.es/pajotape/fallas_infantiles"
        val listaFallas = ArrayList<Falla>()

        val request = Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
            .addHeader("Accept", "application/json").addHeader("Accept-Charset", "es").build()

        val client = OkHttpClient()

        val response = client.newCall(request).execute()

        if(!response.isSuccessful) throw IOException("Unexpected code $response")
        val responseBody = response.body?.string()
        val jsonObject = JSONObject(responseBody)
        val jsonResult = jsonObject.getJSONObject("result")
        val records = jsonResult.getJSONArray("records")

        if(records != null){
            for(i in 0 until records.length()){
                val record = records.getJSONObject(i)
                val id = record.getInt("id_falla")
                val nombre = record.optString("nombre", null)
                val seccion = record.optString("seccion", null)
                val premio = record.optInt("Premio")
                val premioE = record.optInt("PremioE")
                val fallera = record.optString("fallera", null)
                val presidente = record.optString("presidente", null)
                val artista = record.optString("artista", null)
                val lema = record.optString("lema", null)
                val boceto = record.optString("boceto", null)
                val experim = record.optInt("experim")
                val coordenadas = record.optString("geo_point_2d")?.split(",")?.let {
                    Pair(it[0].toDouble(), it[1].toDouble())
                }

                var falla = Falla(id, nombre, seccion, premio, premioE, fallera, presidente, artista, lema, boceto, experim, coordenadas)

                listaFallas.add(falla)
            }
        }

        return listaFallas
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}