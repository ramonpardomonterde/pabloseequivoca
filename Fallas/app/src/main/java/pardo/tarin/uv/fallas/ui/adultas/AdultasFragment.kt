package pardo.tarin.uv.fallas.ui.adultas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pardo.tarin.uv.fallas.Falla
import pardo.tarin.uv.fallas.FallasGeneral
import pardo.tarin.uv.fallas.FallasViewModel
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.databinding.FragmentAdultasBinding
import pardo.tarin.uv.fallas.databinding.FragmentFallasBinding
import java.net.SocketTimeoutException

class AdultasFragment : FallasGeneral() {

    private lateinit var binding: FragmentAdultasBinding
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var dataloaded = false
    var tipo: String = "adultas"
    var _view: View? = null
    var fallasViewModel: FallasViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_adultas)

        if(_view != null) {
            Log.d("ViewNoNull", "View no es null")
            return _view!!
        }

        binding = FragmentAdultasBinding.inflate(inflater, container, false)
        _view = binding.root
        fallasViewModel = ViewModelProvider(requireActivity()).get(FallasViewModel::class.java)
        tipo = arguments?.getString("tipo").toString()


        cargarFallas(tipo)

        //Reintentar cargar los datos
        binding.buttonRecargar.setOnClickListener {
            binding.buttonRecargar.visibility = View.GONE
            binding.textoRecargar.visibility = View.GONE

            cargarFallas(tipo)
        }

        return _view!!
    }

    private fun cargarFallas(tipo: String){

        if (!isNetworkAvailable(requireContext())) {
            // Mostrar un mensaje al usuario indicando que no hay conexión a Internet
            binding.textoRecargar.text = "No hay conexión a Internet"
            binding.textoRecargar.visibility = View.VISIBLE
            binding.buttonRecargar.visibility = View.VISIBLE
            return
        }
        coroutineScope.launch {
            try {
                binding.loadingSpinner.visibility = View.VISIBLE
                // Temporizador para mostrar el botón de reintentar si la carga de datos tarda más de 5 segundos
                launch {
                    delay(10000) // espera 10 segundos
                    if (!dataloaded) {
                        binding.buttonRecargar.visibility = View.VISIBLE
                        binding.textoRecargar.visibility = View.VISIBLE
                        binding.loadingSpinner.visibility = View.GONE
                    }
                }
                /*getFallas("https://mural.uv.es/pajotape/fallas_$tipo") { fallas ->
                    dataloaded = true// cancela el temporizador si los datos se cargan correctamente
                    //originalFallasData = fallas
                    fallasPorSeccion = fallas
                }*/
                fallasViewModel?._fallasAdultas?.observe(viewLifecycleOwner) { fallasAdultas ->
                    if (fallasAdultas != null) {

                        dataloaded = true
                        fallasPorSeccion = fallasAdultas
                        CoroutineScope(Dispatchers.Main).launch {
                            crearVista()
                        }
                    } else {
                        throw Exception("Error al cargar las fallas adultas")
                    }
                }
                //crearVista()

            }catch (e: SocketTimeoutException) {
                Log.d("FallaFragment", e.toString())
                binding.textoRecargar.text = "La conexión ha tardado demasiado. Por favor, inténtalo de nuevo."
                binding.textoRecargar.visibility = View.VISIBLE
                binding.buttonRecargar.visibility = View.VISIBLE
                binding.buttonRecargar.setOnClickListener {
                    cargarFallas(tipo)
                }
                binding.loadingSpinner.visibility = View.GONE
            } catch (e: Exception) {
                Log.d("FallaFragment", e.toString())
                binding.buttonRecargar.visibility = View.VISIBLE
                binding.textoRecargar.visibility = View.VISIBLE
                binding.loadingSpinner.visibility = View.GONE// muestra el botón de reintentar si ocurre una excepción
            }
        }
    }

    private suspend fun crearVista(/*fallas : List<List<Any>>*/) {
        val linearLayout = binding.linearLayoutSecciones // Asegúrate de tener un LinearLayout con este id en tu fragment_infantiles.xml
        //Log.d("Falla", fallasPorSeccion.toString())
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
                //Log.d("Falla", "Nombre: $falla")
                val fallaView = inflater.inflate(R.layout.falla_view, layoutFallasSeccion, false)

                // Aquí puedes configurar los datos de tu falla_view
                // Por ejemplo, si tienes un TextView en tu falla_view, puedes hacer algo como esto:
                val nombre = fallaView.findViewById<TextView>(R.id.falla_name)
                nombre.text = falla.nombre
                val premio = fallaView.findViewById<TextView>(R.id.falla_prize)
                val medalla = fallaView.findViewById<TextView>(R.id.falla_medalla)
                when(falla.premio) {
                    "1" -> medalla.text = "\uD83E\uDD47"
                    "2" -> medalla.text = "\uD83E\uDD48"
                    "3" -> medalla.text = "\uD83E\uDD49"
                    else -> {
                        premio.text = "${getString(R.string.premioSeccion)}: ${falla.premio}"
                        medalla.text = ""
                        premio.visibility = View.VISIBLE
                    }
                }
                if(falla.premioE != "Sin premio") {
                    val premioE = fallaView.findViewById<TextView>(R.id.fallaEG_prize)
                    premioE.visibility = View.VISIBLE
                    premioE.text = "${getString(R.string.premioIG)}: ${falla.premioE}"
                }

                fallaView.setOnClickListener(){
                    if(isAdded) {
                        val bundle = Bundle()
                        bundle.putSerializable("falla", falla)
                        findNavController().navigate(
                            R.id.action_adultasFragment_to_fallaDetails,
                            bundle
                        )
                    }
                }

                layoutFallasSeccion.addView(fallaView)
                layoutFallasSeccion.visibility = View.GONE
                //}
            }
            //Log.d("Falla", "-------------------")
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

        binding.loadingSpinner.visibility = View.GONE

    }
}
        /*
            private var _binding: FragmentAdultasBinding? = null
            val coroutineScope = CoroutineScope(Dispatchers.Main)

            // This property is only valid between onCreateView and
            // onDestroyView.
            private val binding get() = _binding!!

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {
                /*val adultasViewModel =
                    ViewModelProvider(this).get(AdultasViewModel::class.java)*/

                _binding = FragmentAdultasBinding.inflate(inflater, container, false)

                coroutineScope.launch {
                    try {
                        binding.loadingSpinnerAdultas.visibility = View.VISIBLE
                        getFallas("https://mural.uv.es/pajotape/fallas_adultas") { fallas ->
                            originalFallasData = fallas
                            fallasPorSeccion = ordenarPorSeccion(originalFallasData)
                        }

                        binding.loadingSpinnerAdultas.visibility = View.GONE
                        crearVista()
                    } catch (e: Exception) {
                        Log.e("AdultasFragment", "Error al cargar las fallas", e)
                    }
                }

                return binding.root
            }

            private suspend fun crearVista() {
                val linearLayout = binding.linearLayoutSeccionesAdultas // Asegúrate de tener un LinearLayout con este id en tu fragment_infantiles.xml

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
                        if(falla.premioE != "Sin premio") {
                            val premioE = fallaView.findViewById<TextView>(R.id.fallaEG_prize)
                            premioE.visibility = View.VISIBLE
                            premioE.text = "Premio Ingenio y Gracia: ${falla.premioE}"
                        }

                        fallaView.setOnClickListener(){
                            val bundle = Bundle()
                            bundle.putSerializable("falla", falla)
                            //findNavController().navigate(R.id.action_nav_adultas_to_fallaDetails, bundle)
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
            }

            override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
            }*/