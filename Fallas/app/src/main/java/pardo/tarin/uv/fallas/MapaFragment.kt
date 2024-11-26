package pardo.tarin.uv.fallas

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pardo.tarin.uv.fallas.databinding.FragmentMapaBinding

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapaBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var seccionesInfantiles: Array<String>
    private lateinit var seccionesAdultas: Array<String>
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

        seccionesInfantiles = arrayOf(
            "--", getString(R.string.especial), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", getString(R.string.fueraConcurso)
        )
        seccionesAdultas = arrayOf(
            "--", getString(R.string.especial), "1A", "1B", "2A", "2B", "3A", "3B", "3C", "4A", "4B", "4C", "5A", "5B", "5C", "6A", "6B", "6C", "7A", "7B", "7C", "8A", "8B", "8C", getString(R.string.fueraConcurso)
        )

        for(i in 1 until seccionesAdultas.size){
            seccionesAdultas[i] = if(seccionesAdultas[i] == getString(R.string.fueraConcurso)) seccionesAdultas[i] + " ${getString(R.string.seccion)}" else "${getString(R.string.seccion)} " + seccionesAdultas[i]
            seccionesInfantiles[i] = if(seccionesInfantiles[i] == getString(R.string.fueraConcurso)) seccionesInfantiles[i] + " ${getString(R.string.seccion)}" else "${getString(R.string.seccion)} " + seccionesInfantiles[i]
        }

        binding = FragmentMapaBinding.inflate(inflater, container, false)
        _view = binding.root
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, seccionesInfantiles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(Color.BLACK)
                return view
            }
        }
        binding.spinner.adapter = adapter
        binding.spinner.setSelection(0)

        val adapter2 = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, seccionesAdultas) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(Color.BLACK)
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
            animator.duration = 1000
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

        val mapView = binding.mapa
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return _view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

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
                        bundle.putSerializable("falla", falla)
                        findNavController().navigate(
                            R.id.action_mapaFragment_to_fallaDetails,
                            bundle
                        )
                    }
                }

                binding.botonRuta.setOnClickListener {
                    val latLng = LatLng(falla.coordLat!!, falla.coordLong!!)
                    val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${latLng.latitude},${latLng.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
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

        mapaModelView!!._fallasAdultas.observe(viewLifecycleOwner) { fallasAdultas ->
            if (fallasAdultas != null && fallasAdultas.isNotEmpty()) {
                for (i in fallasAdultas[0].drop(1)) {
                    val falla = i as Falla
                    val latLng = LatLng(falla.coordLat!!, falla.coordLong!!)
                    val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                    marker?.tag = falla
                }
            }
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                map.clear()
                mapaModelView!!._fallasInfantiles.observe(viewLifecycleOwner) { fallasInfantiles ->
                    if (fallasInfantiles != null && position < fallasInfantiles.size + 1) {
                        for (i in fallasInfantiles[position - 1].drop(1)) {
                            val falla = i as Falla
                            val latLng = LatLng(falla.coordLat!!, falla.coordLong!!)
                            val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                            marker?.tag = falla
                        }
                    }
                }
                binding.spinner2.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) return
                map.clear()
                mapaModelView!!._fallasAdultas.observe(viewLifecycleOwner) { fallasAdultas ->
                    if (fallasAdultas != null && position < fallasAdultas.size + 1) {
                        for (i in fallasAdultas[position-1].drop(1)) {
                            val falla = i as Falla
                            val latLng = LatLng(falla.coordLat!!, falla.coordLong!!)
                            val marker = map.addMarker(MarkerOptions().position(latLng).title(falla.nombre))
                            marker?.tag = falla
                        }
                    }
                }
                binding.spinner.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
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