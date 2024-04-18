package pardo.tarin.uv.fallas.ui.infantiles

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.fallas.Falla
import pardo.tarin.uv.fallas.FallasGeneral
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.databinding.FragmentInfantilesBinding

class InfantilesFragment: FallasGeneral() {

    private var _binding: FragmentInfantilesBinding? = null
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    var infantilesViewModel: InfantilesViewModel? = null
    private var _view: View? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInfantilesBinding.inflate(inflater, container, false)

        //infantilesViewModel = ViewModelProvider(requireActivity()).get(InfantilesViewModel::class.java)

        coroutineScope.launch {
            try {
                binding.loadingSpinner.visibility = View.VISIBLE
                // Temporizador para mostrar el botón de reintentar si la carga de datos tarda más de 5 segundos
                val job = launch {
                    delay(10000) // espera 10 segundos
                    if (isActive) {
                        binding.buttonRecargarInf.visibility = View.VISIBLE
                        binding.textoRecargarInf.visibility = View.VISIBLE
                        binding.loadingSpinner.visibility = View.GONE
                    }
                }
                getFallas("https://mural.uv.es/pajotape/fallas_adultas") { fallas ->
                    job.cancel() // cancela el temporizador si los datos se cargan correctamente
                    originalFallasData = fallas
                    fallasPorSeccion = ordenarPorSeccion(originalFallasData)
                }
                binding.loadingSpinner.visibility = View.GONE
                crearVista()
            } catch (e: Exception) {
                binding.buttonRecargarInf.visibility = View.VISIBLE
                binding.textoRecargarInf.visibility = View.VISIBLE
                binding.loadingSpinner.visibility = View.GONE// muestra el botón de reintentar si ocurre una excepción
            }
        }

        //Reintentar cargar los datos
        binding.buttonRecargarInf.setOnClickListener {
            binding.buttonRecargarInf.visibility = View.GONE
            binding.textoRecargarInf.visibility = View.GONE
            // vuelve a cargar los datos
            coroutineScope.launch {
                try {
                    binding.loadingSpinner.visibility = View.VISIBLE
                    val job = launch {
                        delay(10000) // espera 10 segundos
                        if (isActive) {
                            binding.buttonRecargarInf.visibility = View.VISIBLE
                            binding.textoRecargarInf.visibility = View.VISIBLE
                            binding.loadingSpinner.visibility = View.GONE
                        }
                    }
                    getFallas("https://mural.uv.es/pajotape/fallas_adultas") { fallas ->
                        job.cancel() // cancela el temporizador si los datos se cargan correctamente
                        originalFallasData = fallas
                        fallasPorSeccion = ordenarPorSeccion(originalFallasData)
                    }
                    binding.loadingSpinner.visibility = View.GONE
                    crearVista()
                } catch (e: Exception) {
                    binding.buttonRecargarInf.visibility = View.VISIBLE
                    binding.textoRecargarInf.visibility = View.VISIBLE
                    binding.loadingSpinner.visibility = View.GONE
                }
            }
        }

        /*lifecycleScope.launchWhenStarted {
            infantilesViewModel?.infantilesPorSeccion?.collect { fallas ->
                // Este bloque de código se ejecutará cuando los datos estén listos
                withContext(Dispatchers.Main) {
                    binding.loadingSpinner.visibility = View.VISIBLE
                    crearVista(fallas)
                    binding.loadingSpinner.visibility = View.GONE
                }
            }
        }*/
        /*if (infantilesViewModel?._view == null) {
            Log.d("ViewNull", "View es null")
            _binding = FragmentInfantilesBinding.inflate(inflater, container, false)
            infantilesViewModel?._view = binding.root

            lifecycleScope.launchWhenStarted {
                infantilesViewModel?.infantilesPorSeccion?.collect { fallas ->
                    binding.loadingSpinner.visibility = View.VISIBLE
                    crearVista(fallas)
                }
                binding.loadingSpinner.visibility = View.GONE
            }
        } else {
            Log.d("ViewNoNull", "View no es null")
        }

        return infantilesViewModel?._view!!*/

        /*val textView: TextView = binding.textGallery
        textView.text = "This is infantiles Fragment"*/
        return binding.root
    }

    private suspend fun crearVista(/*fallas : List<List<Any>>*/) {
        val linearLayout = binding.linearLayoutSecciones // Asegúrate de tener un LinearLayout con este id en tu fragment_infantiles.xml
        Log.d("Falla", fallasPorSeccion.toString())
        for (i in fallasPorSeccion) {
            val inflater = LayoutInflater.from(context)
            val seccionView = inflater.inflate(R.layout.seccion_view, linearLayout, false)

            // Aquí puedes configurar los datos de tu seccion_view
            // Por ejemplo, si tienes un TextView en tu seccion_view, puedes hacer algo como esto:
            val name = seccionView.findViewById<TextView>(R.id.seccion_name)
            name.text = i[0].toString()

            val layoutFallasSeccion = seccionView.findViewById<LinearLayout>(R.id.linear_layoutSecciones)

            for (j in i.drop(1).sortedBy { (it as Falla).premio?.toIntOrNull() ?: Int.MAX_VALUE}) {
                //if(falla is Falla) {
                val falla = j as Falla
                Log.d("Falla", "Nombre: $falla")
                val fallaView = inflater.inflate(R.layout.falla_view, layoutFallasSeccion, false)

                // Aquí puedes configurar los datos de tu falla_view
                // Por ejemplo, si tienes un TextView en tu falla_view, puedes hacer algo como esto:
                val nombre = fallaView.findViewById<TextView>(R.id.falla_name)
                nombre.text = falla.nombre
                val premio = fallaView.findViewById<TextView>(R.id.falla_prize)
                val medalla = fallaView.findViewById<TextView>(R.id.falla_section)
                when(falla.premio) {
                    "1" -> medalla.text = "\uD83E\uDD47"
                    "2" -> medalla.text = "\uD83E\uDD48"
                    "3" -> medalla.text = "\uD83E\uDD49"
                    else -> {
                        premio.text = "Premio Sección: ${falla.premio}"
                        medalla.text = ""
                        premio.visibility = View.VISIBLE
                    }
                }
                if(falla.premioE != 0) {
                    val premioE = fallaView.findViewById<TextView>(R.id.fallaEG_prize)
                    premioE.visibility = View.VISIBLE
                    premioE.text = "Premio Ingenio y Gracia: ${falla.premioE}"
                }

                fallaView.setOnClickListener(){
                    if(isAdded) {
                        val bundle = Bundle()
                        bundle.putSerializable("falla", falla)
                        findNavController().navigate(
                            R.id.action_nav_infantiles_to_fallaDetails,
                            bundle
                        )
                    }
                }

                layoutFallasSeccion.addView(fallaView)
                layoutFallasSeccion.visibility = View.GONE
                //}
            }
            Log.d("Falla", "-------------------")
            linearLayout.addView(seccionView)
            val plusmenos = seccionView.findViewById<ImageView>(R.id.plusminus)
            plusmenos.setOnClickListener {
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


        Log.d("Falla", "Creando vista fallas infantiles")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}