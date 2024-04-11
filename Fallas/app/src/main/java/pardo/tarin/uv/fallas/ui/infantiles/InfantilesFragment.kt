package pardo.tarin.uv.fallas.ui.infantiles

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import pardo.tarin.uv.fallas.Falla
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.databinding.FragmentInfantilesBinding
import java.io.IOException

class InfantilesFragment : Fragment() {

    private var _binding: FragmentInfantilesBinding? = null
    private lateinit var originalFallasData: ArrayList<Falla>
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var fallasPorSeccion: List<List<Any>> = listOf()

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

        coroutineScope.launch {
            getFallasInfantiles { fallas ->
                originalFallasData = fallas

                fallasPorSeccion = ordenarPorSeccion(originalFallasData)
            }

            /*val spinners: List<Spinner> = listOf(
                binding.spinnerSeccionE,
                binding.spinnerSeccion1,
                binding.spinnerSeccion2,
                binding.spinnerSeccion3,
                binding.spinnerSeccion4,
                binding.spinnerSeccion5,
                binding.spinnerSeccion6,
                binding.spinnerSeccion7,
                binding.spinnerSeccion8,
                binding.spinnerSeccion9,
                binding.spinnerSeccion10,
                binding.spinnerSeccion11,
                binding.spinnerSeccion12,
                binding.spinnerSeccion13,
                binding.spinnerSeccion14,
                binding.spinnerSeccion15,
                binding.spinnerSeccion16,
                binding.spinnerSeccion17,
                binding.spinnerSeccion18,
                binding.spinnerSeccion19,
                binding.spinnerSeccion20,
                binding.spinnerSeccion21,
                binding.spinnerSeccion22
            )*/

            /*for ((index, fila) in fallasPorSeccion.withIndex()) {
                // Asegúrate de no salirte del rango de los Spinners
                if (index >= spinners.size) break

                val spinner = spinners[index]
                val adapter = CustomArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fila.toMutableList())
                spinner.adapter = adapter
                spinner.isEnabled = true // Deshabilita la interacción con el Spinner
                spinner.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position != 0) {  // Evita abrir el AdultasFragment cuando se selecciona el nombre fijo
                            findNavController().navigate(R.id.action_nav_infantiles_to_nav_adultas)

                            // Vuelve a establecer el elemento seleccionado al nombre fijo
                            spinner.setSelection(0)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No hacer nada
                    }
                }
            }*/

            val linearLayout = binding.linearLayoutSecciones // Asegúrate de tener un LinearLayout con este id en tu fragment_infantiles.xml

            for (i in fallasPorSeccion) {
                val inflater = LayoutInflater.from(context)
                val seccionView = inflater.inflate(R.layout.seccion_view, linearLayout, false)

                // Aquí puedes configurar los datos de tu seccion_view
                // Por ejemplo, si tienes un TextView en tu seccion_view, puedes hacer algo como esto:
                val name = seccionView.findViewById<TextView>(R.id.seccion_name)
                name.text = i[0].toString()

                val layoutFallasSeccion = seccionView.findViewById<LinearLayout>(R.id.linear_layoutSecciones)

                for (falla in i.drop(1)) {
                    //if(falla is Falla) {
                        Log.d("Falla", "Nombre: $falla")
                        val fallaView = inflater.inflate(R.layout.falla_view, layoutFallasSeccion, false)

                        // Aquí puedes configurar los datos de tu falla_view
                        // Por ejemplo, si tienes un TextView en tu falla_view, puedes hacer algo como esto:
                        val nombre = fallaView.findViewById<TextView>(R.id.falla_name)
                        nombre.text = falla.toString()
                        /*val premio = fallaView.findViewById<TextView>(R.id.falla_prize)
                        premio.text = falla.premio.toString()*/

                        layoutFallasSeccion.addView(fallaView)
                        layoutFallasSeccion.visibility = View.GONE
                    //}
                }
                Log.d("Falla", "-------------------")
                linearLayout.addView(seccionView)
                val plusmenos = seccionView.findViewById<ImageView>(R.id.plusminus)
                seccionView.setOnClickListener {
                    val tag = plusmenos.tag
                    if (tag == null || tag == "plus") {
                        plusmenos.setImageResource(R.drawable.menos) // Asegúrate de tener un recurso de imagen llamado 'minus'
                        plusmenos.tag = "minus"
                        layoutFallasSeccion.visibility = View.VISIBLE
                    } else {
                        plusmenos.setImageResource(R.drawable.plus) // Asegúrate de tener un recurso de imagen llamado 'plus'
                        plusmenos.tag = "plus"
                        layoutFallasSeccion.visibility = View.GONE
                    }
                }
            }

        }

        /*val textView: TextView = binding.textGallery
        textView.text = "This is infantiles Fragment"*/
        return binding.root
    }

    suspend fun getFallasInfantiles(callback: (ArrayList<Falla>) -> Unit) = withContext(Dispatchers.IO) {
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
                val geo = record.getJSONObject("geo_point_2d")
                val lat = geo.optDouble("lat")
                val lon = geo.optDouble("lon")
                val coordenadas = Pair(lat, lon)

                val falla = Falla(id, nombre, seccion, premio, premioE, fallera, presidente, artista, lema, boceto, experim, coordenadas)

                listaFallas.add(falla)
            }
        }

        callback(listaFallas)
    }

    fun ordenarPorSeccion(originalFallasData: ArrayList<Falla>): List<List<Any>> {
        val fallasPorSeccion: Map<String, List<Falla>> = originalFallasData.groupBy { it.seccion.toString() }

        val matriz = fallasPorSeccion.map { (seccion, fallas) ->
            val fila = mutableListOf<Any>()
            if(seccion == "IE"){
                fila.add("Sección Especial")
            }
            else {
                fila.add("Sección $seccion") // Se añade el nombre de la sección a la fila
            }
            fila.addAll(fallas.map { it.nombre ?: "Sin nombre" })
            fila
        }

        val matrizIE = matriz.filter { it[0] == "Sección Especial" }
        val matrizNoIE = matriz.filter { it[0] != "Sección Especial" }.sortedBy {
            val seccion = it[0].toString().removePrefix("Sección ").toIntOrNull() ?: Int.MAX_VALUE
            seccion
        }

        val matrizOrdenada = matrizIE + matrizNoIE

        /*val fila = matrizOrdenada[2]
        for (i in 1 until fila.size) {
            val falla = fila[i] as Falla
            val nombreFalla = falla.nombre ?: "Sin nombre"
            val seccion = falla.seccion ?: "Sin sección"
            Log.d("Falla", "Sección $seccion - $nombreFalla")
        }*/

        return matrizOrdenada
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class CustomArrayAdapter(context: Context, resource: Int, objects: MutableList<Any>) : ArrayAdapter<Any>(context, resource, objects) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view as TextView

            if (position == 0) {
                // Si es el primer elemento, cambia el estilo a negrita y aumenta el tamaño de letra
                textView.setTypeface(null, Typeface.BOLD)
                textView.textSize = 20f
            } else {
                // Para los demás elementos, usa el estilo normal y el tamaño de letra predeterminado
                textView.setTypeface(null, Typeface.NORMAL)
                textView.textSize = 16f
            }

            return view
        }

        override fun isEnabled(position: Int): Boolean {
            return true // Deshabilita la interacción con todos los elementos
        }

        override fun getCount(): Int {
            return super.getCount()  // Muestra todos los elementos
        }
    }
}