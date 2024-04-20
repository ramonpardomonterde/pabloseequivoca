package pardo.tarin.uv.fallas

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pardo.tarin.uv.fallas.databinding.FragmentMapaBinding

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapaBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val seccionesInfantiles = arrayOf(
        "--", "Especial", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "SFC"
    )
    private val seccionesAdultas = arrayOf(
        "--", "Especial", "1A", "1B", "2A", "2B", "3A", "3B", "3C", "4A", "4B", "4C", "5A", "5B", "5C", "6A", "6B", "6C", "7A", "7B", "7C", "8A", "8B", "8C", "SFC"
    )
    private var mapaModelView: FallasViewModel? = null
    private var _view: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(_view != null) {
            Log.d("ViewNoNull", "View no es null")
            return _view!!
        }

        binding = FragmentMapaBinding.inflate(inflater, container, false)
        _view = binding.root
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, seccionesInfantiles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(Color.BLACK) // Cambia esto al color que desees
                return view
            }
        }
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0)

        val adapter2 = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, seccionesAdultas) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(Color.BLACK) // Cambia esto al color que desees
                return view
            }
        }

        binding.spinner2.adapter = adapter2
        binding.spinner2.setSelection(1)

        val button = binding.button
        val linearLayout = binding.filtrosSecciones
        val constraintLayout = binding.contraint

        var isOpen = false
        var drawable = ResourcesCompat.getDrawable(resources, R.drawable.plus, null)
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

        button.setOnClickListener {
            val layoutParams = linearLayout.layoutParams as ConstraintLayout.LayoutParams
            val startBias = if (isOpen) 0f else -500f
            val endBias = if (isOpen) -500f else 0f
            drawable = if (isOpen) ResourcesCompat.getDrawable(resources, R.drawable.plus, null) else ResourcesCompat.getDrawable(resources, R.drawable.menos, null)
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
            val animator = ValueAnimator.ofFloat(startBias, endBias)
            animator.duration = 1000 // Duración de la animación en milisegundos
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Float
                layoutParams.topMargin = animatedValue.toInt()
                linearLayout.layoutParams = layoutParams
                constraintLayout.requestLayout()
            }
            animator.start()
            isOpen = !isOpen
        }


            mapaModelView = ViewModelProvider(this)[FallasViewModel::class.java]
        /*val toggleButtons = listOf(
            binding.toggleButtonSE,
            binding.toggleButtonS1,
            binding.toggleButtonS2,
            binding.toggleButtonS3,
            binding.toggleButtonS4,
            binding.toggleButtonS5,
            binding.toggleButtonS6,
            binding.toggleButtonS7,
            binding.toggleButtonS8,
            binding.toggleButtonS9,
            binding.toggleButtonS10,
            binding.toggleButtonS11,
            binding.toggleButtonS12,
            binding.toggleButtonS13,
            binding.toggleButtonS14,
            binding.toggleButtonS15,
            binding.toggleButtonS16,
            binding.toggleButtonS17,
            binding.toggleButtonS18,
            binding.toggleButtonS19,
            binding.toggleButtonS20,
            binding.toggleButtonS21,
            binding.toggleButtonS22,
            binding.toggleButtonSFC
        )

        toggleButtons.forEach { toggleButton ->
            toggleButton.setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    toggleButtons.forEach { it.isChecked = it == button }
                } else {
                    // Si todos los botones están desactivados, vuelve a activar el botón actual
                    if (toggleButtons.none { it.isChecked }) {
                        button.isChecked = true
                    }
                }
            }
        }*/
        val mapView = binding.mapa
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return _view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        var seleccionado = "infantiles"

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true
        val locationButton = (binding.mapa.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
        // Posición en la parte inferior derecha
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //map.addMarker(MarkerOptions().position(currentLatLng).title("Mi ubicación"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }

        map.setOnMarkerClickListener { marker ->
            val tag = marker.tag
            if (tag is Falla) {
                val falla = tag
                binding.layoutFallaMapa.visibility = View.VISIBLE
                binding.nombreFallaMapa.text = falla.nombre
                if(falla.seccion == "IE" || falla.seccion == "E"){
                    binding.seccionFallaMapa.text = "Sección Especial"
                }
                else if (falla.seccion == "FC"){
                    binding.seccionFallaMapa.text = "Sección Fuera de Concurso"
                }
                else if (falla.seccion != null){
                    binding.seccionFallaMapa.text = "Sección ${falla.seccion}"
                }

                binding.botonMasDetalle.setOnClickListener {
                    if (falla != null) {
                        val bundle = Bundle()
                        bundle.putSerializable(seleccionado, falla)
                        findNavController().navigate(
                            R.id.action_mapaFragment_to_fallaDetails,
                            bundle
                        )
                    }
                }
            }
            true
        }

        binding.botonClose.setOnClickListener {
            binding.layoutFallaMapa.visibility = View.GONE
        }

        map.setOnMapClickListener { _ ->
            binding.layoutFallaMapa.visibility = View.GONE
        }

        val goldIcon = BitmapDescriptorFactory.fromResource(R.drawable.medalla_de_oro)

        mapaModelView!!._fallasAdultas.observe(viewLifecycleOwner) { fallasAdultas ->
            if (fallasAdultas != null && fallasAdultas.isNotEmpty()) {
                for (i in fallasAdultas[0].drop(1)) {
                    val falla = i as Falla
                    val coordenadas = falla.coordenadas
                    val latLng = LatLng(coordenadas!!.first, coordenadas.second)
                    /*if(falla.premio == "1"){
                        val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre).icon(goldIcon))
                        marker?.tag = falla
                    }
                    else{
                        val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                        marker?.tag = falla
                    }*/
                    val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                    marker?.tag = falla
                }
            }
            seleccionado = "adultas"
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //val seccion = secciones[position]
                if(position == 0) return
                map.clear()
                mapaModelView!!._fallasInfantiles.observe(viewLifecycleOwner) { fallasInfantiles ->
                    if (fallasInfantiles != null && position < fallasInfantiles.size + 1) {
                        for (i in fallasInfantiles[position - 1].drop(1)) {
                            val falla = i as Falla
                            val coordenadas = falla.coordenadas
                            val latLng = LatLng(coordenadas!!.first, coordenadas.second)
                            val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                            marker?.tag = falla
                        }
                    }
                }
                seleccionado = "infantiles"
                binding.spinner2.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //viewModel.getFallas("Sección Especial")
            }
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //val seccion = secciones[position]
                if(position == 0) return
                map.clear()
                mapaModelView!!._fallasAdultas.observe(viewLifecycleOwner) { fallasAdultas ->
                    if (fallasAdultas != null && position < fallasAdultas.size + 1) {
                        for (i in fallasAdultas[position-1].drop(1)) {
                            val falla = i as Falla
                            val coordenadas = falla.coordenadas
                            val latLng = LatLng(coordenadas!!.first, coordenadas.second)
                            val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                            marker?.tag = falla
                        }
                    }
                }
                seleccionado = "adultas"
                binding.spinner.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //viewModel.getFallas("Sección Especial")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapa.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapa.onResume()
    }

    override fun onPause() {
        binding.mapa.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapa.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.mapa.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapa.onLowMemory()
    }
}