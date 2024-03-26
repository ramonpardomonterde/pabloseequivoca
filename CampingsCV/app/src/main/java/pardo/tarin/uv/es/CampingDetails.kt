package pardo.tarin.uv.es

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.es.base_datos.AppDatabase
import pardo.tarin.uv.es.databinding.FragmentCampingDetailsBinding

class CampingDetails : Fragment() {

    private lateinit var binding: FragmentCampingDetailsBinding
    private lateinit var camping: Camping

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Recuperar el objeto Camping del Bundle
        camping = arguments?.getSerializable("camping") as Camping
        // Establecer el título de la actividad al nombre del camping
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Habilitar el botón de retroceso en la barra de acción
        }
        // Establecer el título de la actividad al nombre del camping

        binding = FragmentCampingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        comprobarFavorito(camping)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indicar que este fragmento manejará los eventos del menú de opciones
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_camping, menu) // Inflar el menú de opciones
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ver_mapa -> {
                // Abrir el mapa del camping en Google Maps
                val uri = "geo:0,0?q=${camping.direccion}, ${camping.municipio}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
            R.id.visitar_web -> {
                // Abrir la web del camping en un navegador
                val url = camping.web
                val intent = Intent(Intent.ACTION_VIEW)
                if(!url.startsWith("http://") && !url.startsWith("https://") && url.length <= 1){
                    Toast.makeText(context, "Página web no disponible", Toast.LENGTH_SHORT).show()
                } else {
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
            android.R.id.home -> {
                findNavController().navigateUp() // Navegar hacia atrás
                return true
            }
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(activity as AppCompatActivity).supportActionBar?.title = camping.nombre
        // Actualizar la vista con los datos del camping
        binding.fab.setOnClickListener {
            // Abrir el mapa del camping en Google Maps
            val uri = "geo:0,0?q=${camping.direccion}, ${camping.municipio}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }

        binding.fabFavorite.setOnClickListener {
            // Comprueba si el botón de favoritos actualmente tiene la imagen de estrella llena
            if (binding.fabFavorite.drawable.constantState == resources.getDrawable(android.R.drawable.btn_star_big_on).constantState) {
                // Si es así, cambia la imagen a la estrella vacía
                binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                //campingDao.delete(camping)
                borrarFavorito(camping)
                Toast.makeText(
                    context,
                    "'${camping.nombre}' eliminado de favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Si no, cambia la imagen a la estrella llena
                binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                //campingDao.insertCamping(camping)
                añadirFavorito(camping)
                Toast.makeText(
                    context,
                    "'${camping.nombre}' añadido a favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvCampingName.text = "${camping.nombre}\n"
        binding.tvCampingCategory.text = createSpannable("Categoria", camping.categoria)
        binding.tvCampingSpots.text = createSpannable("Plazas", camping.plazas.toString())
        binding.tvCampingAddress.text = createSpannable("Direccion", camping.direccion)
        binding.tvCampingWebsite.text = createSpannable("Web", camping.web)
        binding.tvCampingCity.text = createSpannable("Municipio", camping.municipio)
        CoroutineScope(Dispatchers.IO).launch {
            setUpMap(savedInstanceState)
        }
    }

    fun createSpannable(title: String, value: String): SpannableString {
        val finalValue = if (value.isEmpty()) "No disponible" else value
        val text = "$title \n$finalValue\n"
        val spannable = SpannableString(text)
        spannable.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    fun añadirFavorito(camping: Camping) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "campings_database"
        ).build()
        val campingDao = db.campingDao()
        val camping = Camping(camping.id, camping.categoria, camping.nombre, camping.municipio, camping.direccion, camping.web, camping.plazas)
        GlobalScope.launch {
            try{
                campingDao.insertCamping(camping)
                Log.d("FavoritosDetails", campingDao.getAll().toString())
                Log.d("CampingDetails", "Camping ${camping.nombre} añadido a favoritos")
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al añadir el camping a favoritos: ${e.message}")
            }
        }
    }

    fun borrarFavorito(camping: Camping) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "campings_database"
        ).build()
        val campingDao = db.campingDao()
        GlobalScope.launch {
            try{
                campingDao.delete(camping)
                Log.d("FavoritosDetails", campingDao.getAll().toString())
                Log.d("CampingDetails", "Camping ${camping.nombre} eliminado de favoritos")
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al eliminar el camping de favoritos: ${e.message}")
            }
        }
    }

    fun comprobarFavorito(camping: Camping) {
        GlobalScope.launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "campings_database"
            ).build()
            val campingDao = db.campingDao()
            val isFavourite = campingDao.getCamping(camping.id) != null

            withContext(Dispatchers.Main) {
                if (isFavourite) {
                    binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    binding.fabFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }

    private suspend fun setUpMap(savedInstanceState: Bundle?){
        withContext(Dispatchers.Main) {
            var geocoder = Geocoder(requireContext())
            val string = "${camping.direccion}, ${camping.municipio}"
            try {
                val addresses: List<Address>? = geocoder.getFromLocationName(string, 1)
                if (addresses != null) {
                    if(addresses.isNotEmpty()) {
                        val address: Address = addresses[0]
                        val latitude: Double = address.latitude
                        val longitude: Double = address.longitude
                        val location = LatLng(latitude, longitude)
                        binding.mapView.onCreate(savedInstanceState)
                        binding.mapView.getMapAsync { googleMap ->
                            googleMap.addMarker(MarkerOptions().position(location).title(camping.nombre))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                            googleMap.setOnMapClickListener { latLng ->
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Abrir Maps")
                                    .setMessage("¿Quieres abrir Google Maps para obtener la ruta?")
                                    .setPositiveButton("Sí") { _, _ ->
                                        val uri =
                                            "geo:${latLng.latitude},${latLng.longitude}?q=${camping.direccion}, ${camping.municipio}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                        intent.setPackage("com.google.android.apps.maps")
                                        startActivity(intent)
                                    }
                                    .setNegativeButton("No", null)
                                    .show()
                            }
                        }
                    } else {
                        Log.e("CampingDetails", "No se ha encontrado la dirección")
                    }
                } else {
                    Log.e("CampingDetails", "No se ha encontrado la dirección 2")
                }
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al obtener la ubicación del camping: ${e.message}")
            }
        }
    }
}