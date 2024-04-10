package pardo.tarin.uv.es

import MyAdapter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.InputStream
import pardo.tarin.uv.es.databinding.FragmentCampingListBinding
import java.io.IOException
import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.location.Address
import android.location.Geocoder
import android.widget.FrameLayout
import android.widget.Toast
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import pardo.tarin.uv.es.base_datos.AppDatabase
import pardo.tarin.uv.es.databinding.CampingViewBinding

class CampingList : Fragment(), OnDataLoaded {
    private lateinit var myAdapter: MyAdapter
    private lateinit var binding: FragmentCampingListBinding
    private lateinit var bindingCV: CampingViewBinding
    private lateinit var binding_Camping_View: CampingViewBinding
    private var nombre = 0
    //private val campingsData: ArrayList<Camping> by lazy { getData() }
    private lateinit var originalCampingsData: ArrayList<Camping>
    private var campingsData: ArrayList<Camping> = ArrayList()
    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private var ubiDispositivo: Location = Location("")
    private var areButtonsVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCampingListBinding.inflate(inflater, container, false)
        bindingCV = CampingViewBinding.inflate(layoutInflater)
        CoroutineScope(Dispatchers.Main).launch {
            val idDataset = withContext(Dispatchers.IO) { getIdDataset() }
            println("Dataset = $idDataset")
            if (idDataset.isNotEmpty()) {
                originalCampingsData = withContext(Dispatchers.IO) { getDataHttp(idDataset, this@CampingList) }
                myAdapter.actualizarDatos(originalCampingsData)
            }
        }

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //ubiDispositivo = obtenerUbicacion()

        //binding = FragmentCampingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indicar que este fragmento manejará los eventos del menú de opciones
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu) // Inflar el menú de opciones
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sortButton = view.findViewById<FloatingActionButton>(R.id.sortButton)
        val sortByName = view.findViewById<FrameLayout>(R.id.frameLayout)
        val sortByRating = view.findViewById<FloatingActionButton>(R.id.sortByRating)

        sortByName.visibility = View.INVISIBLE
        sortByRating.visibility = View.INVISIBLE

        sortButton.setOnClickListener {
            // Convierte las distancias de dp a px
            val distanceSortByName = if (areButtonsVisible) -10f else -62 * Resources.getSystem().displayMetrics.density
            val distanceSortByRating = if (areButtonsVisible) -10f else -110 * Resources.getSystem().displayMetrics.density

            // Crea las animaciones
            val animatorSortByName = ObjectAnimator.ofFloat(sortByName, "translationY", distanceSortByName)
            val animatorSortByRating = ObjectAnimator.ofFloat(sortByRating, "translationY", distanceSortByRating)

            // Configura las animaciones
            animatorSortByName.duration = 500 // Duración en milisegundos
            animatorSortByRating.duration = 500 // Duración en milisegundos

            // Agrega un listener a la animación
            animatorSortByName.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // Código para ejecutar cuando la animación comienza
                    if (!areButtonsVisible) {
                        sortByName.visibility = View.VISIBLE
                        sortByRating.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    // Código para ejecutar cuando la animación termina
                    if (areButtonsVisible) {
                        sortByName.visibility = View.INVISIBLE
                        sortByRating.visibility = View.INVISIBLE
                    }

                    // Cambia el estado de visibilidad de los botones
                    areButtonsVisible = !areButtonsVisible
                }

                override fun onAnimationCancel(animation: Animator) {
                    // Código para ejecutar cuando la animación es cancelada
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // Código para ejecutar cuando la animación se repite
                }
            })

            if(!areButtonsVisible) {
                binding.sortButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                binding.sortButton.setImageResource(android.R.drawable.ic_menu_sort_by_size)
            }

            // Inicia las animaciones
            animatorSortByName.start()
            animatorSortByRating.start()
        }

        sortByName.setOnClickListener {
            val criterio: String
            if (nombre == 0){
                ordenarAscendente("nombre")
                criterio = "Nombre (A - Z)"
                binding.sortByNameText.text = "Z->A"
                nombre = 1
            } else {
                ordenarDescendente("nombre")
                criterio = "Nombre (Z - A)"
                binding.sortByNameText.text = "A->Z"
                nombre = 0
            }
            mostrarOrdenadoPor(criterio)
        }

        sortByRating.setOnClickListener {
            ordenarDescendente("categoria")
            mostrarOrdenadoPor("Categoria")
        }

        binding.rv.layoutManager = LinearLayoutManager(activity)
        myAdapter = MyAdapter(campingsData, object: MyAdapter.CampingItemListener {
            override fun onItemClick(camping: Camping) {
                val bundle = Bundle()
                bundle.putSerializable("camping", camping)
                findNavController().navigate(R.id.action_campingList_to_campingDetails2, bundle)
            }
        })
        binding.rv.adapter = myAdapter // Asignar el adaptador al RecyclerView

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        searchView.queryHint = "Buscar por nombre"
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.setOnCloseListener {
            searchView.setQuery("", false)
            true
        }
        val ordenadopor = view.findViewById<TextView>(R.id.ordenadopor)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    campingsData = ArrayList(originalCampingsData) // Restablecer a la lista original si el texto está vacío
                    myAdapter.actualizarDatos(campingsData)
                } else {
                    campingsData = ArrayList(originalCampingsData.filter { camping ->
                        camping.nombre.contains(newText, ignoreCase = true)
                    })
                    myAdapter.actualizarDatos(campingsData)
                }
                if (newText.isNullOrEmpty()) {
                    ordenadopor.visibility = View.INVISIBLE
                }
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        campingsData.forEach { camping ->
            comprobarFavorito(camping)
        }
    }

    /*private fun getData(): ArrayList<Camping> {
        val listaCampings = ArrayList<Camping>()
        val rawResourceId = R.raw.campings
        val jsonFileContent = readJsonFromRaw(resources, rawResourceId)

        val jsonObject = JSONObject(jsonFileContent)
        val jsonResult = jsonObject.getJSONObject("result")
        val recordsArray = jsonResult.getJSONArray("records")
        //getData2()
        for (i in 0 until recordsArray.length()) {
            val record = recordsArray.getJSONObject(i)

            // Obtener datos específicos del objeto 'record'
            /*val signatura = record.getString("Signatura")
            val estado = record.getString("Estado")
            val provincia = record.getString("Provincia")
            val cp = record.getInt("CP")
            val tipoVia = record.getString("Tipo de Vía")
            val via = record.getString("Vía")
            val numero = record.getString("Número")
            val email = record.getString("Email")
            val modalidad = record.getString("Modalidad")
            val numParcelas = record.getInt("Núm. Parcelas")
            val plazasParcela = record.getString("Plazas Parcela")
            val numBungalows = record.getString("Núm. Bungalows")
            val plazaBungalows = record.getString("Plaza Bungalows")
            val supLibreAcampada = record.getString("Sup. Libre Acampada")
            val plazasLibreAcampada = record.getString("Plazas Libre Acampada")
            val fechaAlta = record.getString("Fecha Alta")
            val fechaBaja = record.getString("Fecha Baja")
            val periodo = record.getString("Periodo")
            val diasPeriodo = record.getString("Días Periodo")*/
            val id = record.getInt("_id")
            val nombre = record.getString("Nombre")
            val plazas = record.getInt("Plazas")
            val categoria = record.getString("Cod. Categoria")
            val direccion = record.getString("Direccion")
            val municipio = record.getString("Municipio")
            var web = record.getString("Web")
            if(web.length > 1 && web[0] != 'h') {
                web = "https://$web"
            } else if(web.length <= 1) {
                web = ""
            }

            val cat: String = if (categoria.isNotEmpty() && categoria[0].isDigit()) {
                // Si es un número, devolver solo el primer carácter
                categoria[0].toString()
            } else {
                categoria
            }

            // Crear un nuevo objeto Camping y agregarlo a la lista
            val camping = Camping(id, cat, nombre, municipio, direccion, web, plazas)
            listaCampings.add(camping)
        }

        return listaCampings


    fun getDataHttp(idDataset: String, callback: OnDataLoaded): ArrayList<Camping> {
        val url = "https://dadesobertes.gva.es/api/3/action/datastore_search?id=$idDataset"
        val listaCampings = ArrayList<Camping>()

        if (idDataset.isEmpty()) {
            Log.e("CampingList", "Id of the dataset is empty")
            return ArrayList()
        }

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
                val id = record.getInt("_id")
                val nombre = record.getString("Nombre")
                val plazas = record.getInt("Plazas")
                val categoria = record.getString("Cod. Categoria")
                val direccion = record.getString("Direccion")
                val municipio = record.getString("Municipio")
                var web = record.getString("Web")
                if(web.length > 1 && web[0] != 'h') {
                    web = "https://$web"
                } else if(web.length <= 1) {
                    web = ""
                }
                val cat: String = if (categoria.isNotEmpty() && categoria[0].isDigit()) {
                    // Si es un número, devolver solo el primer carácter
                    categoria[0].toString()
                } else {
                    categoria
                }
                var camping = Camping(id, cat, nombre, municipio, direccion, web, plazas)

                listaCampings.add(camping)
            }
        }

        callback.onDataLoaded()

        return listaCampings
    }

    suspend fun getIdDataset(): String {
        val url = "https://dadesobertes.gva.es/api/3/action/package_search?q=dades-turisme-campings-comunitat-valenciana"
        var idDataset = ""
        val request = Request.Builder().url(url).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
            .addHeader("Accept", "application/json").addHeader("Accept-Charset", "es").build()

        val client = OkHttpClient()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val responseBody = response.body?.string()
        val jsonObject = JSONObject(responseBody)
        val jsonResult = jsonObject.getJSONObject("result")
        val jsonRecords = jsonResult.getJSONArray("results")
        val resources = jsonRecords.getJSONObject(0).getJSONArray("resources")

        if (resources != null) {
            val record = resources.getJSONObject(2)
            idDataset = record.getString("id")
            Log.d("CampingList", "Id del dataset: $idDataset")
        } else {
            Log.e("CampingList", "No se ha podido obtener el id del dataset")
        }

        return idDataset
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val criterio: String
        when (item.itemId){
            R.id.nombre -> {
                if (nombre == 0){
                    ordenarAscendente("nombre")
                    criterio = "Nombre (A - Z)"
                    item.title = "Nombre (Z - A)"
                    nombre = 1
                } else {
                    ordenarDescendente("nombre")
                    criterio = "Nombre (Z - A)"
                    item.title = "Nombre (A - Z)"
                    nombre = 0
                }
                mostrarOrdenadoPor(criterio)
            }
            R.id.categoria -> {
                ordenarDescendente("categoria")
                criterio = "Categoria"
                mostrarOrdenadoPor(criterio)
            }
            R.id.favoritos -> {
                findNavController().navigate(R.id.action_campingList_to_campingFavouriteList)
            }
        }

        return true
    }

    private fun ordenarAscendente(dato: String) {
        val campingsOrdenados: List<Camping> = when (dato) {
            "nombre" -> campingsData.sortedBy { it.nombre }
            "categoria" -> campingsData.sortedBy { it.categoria }
            else -> campingsData
        }

        myAdapter.actualizarDatos(ArrayList(campingsOrdenados))
    }

    private fun ordenarDescendente(dato: String) {
        val campingsOrdenados: List<Camping> = when (dato) {
            "nombre" -> campingsData.sortedByDescending { it.nombre }
            "categoria" -> campingsData.sortedByDescending { it.categoria }
            else -> campingsData // O cualquier otra lógica para el caso predeterminado
        }

        myAdapter.actualizarDatos(ArrayList(campingsOrdenados))
    }

    private fun mostrarOrdenadoPor(criterio: String) {
        binding.ordenadopor.visibility = View.VISIBLE
        binding.ordenadopor.text = "Ordenado por: $criterio"
    }

    override fun onDataLoaded() {
        // Aquí puedes manejar lo que sucede cuando se completa la carga de datos
    }

    /*private fun obtenerUbicacion(): Location {
        var latlong = Location("")
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no se han concedido
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return latlong
        }

        /*fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Usar la ubicación aquí
                latlong.latitude = location?.latitude ?: 0.0
                latlong.longitude = location?.longitude ?: 0.0
                println("Latitud: ${location?.latitude}, Longitud: ${location?.longitude}")
            }*/

        return latlong
    }

    public suspend fun calcularDistancia(camping: Camping){
        withContext(Dispatchers.Main) {
            if (!isAdded) { // Check if the fragment is added to the activity
                return@withContext
            }
            var geocoder = Geocoder(requireContext())
            val string = "${camping.direccion}, ${camping.municipio}"
            try {
                val addresses: List<Address>? = geocoder.getFromLocationName(string, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address: Address = addresses[0]
                        val location = Location("")
                        location.latitude = address.latitude
                        location.longitude =  address.longitude
                        val distancia = ubiDispositivo.distanceTo(location)
                        val binding_Camping_View = CampingViewBinding.inflate(layoutInflater)
                        Log.d("CampingList", "Distancia: $distancia")
                        //binding_Camping_View.municipio.text = location.toString()
                        //binding_Camping_View.municipio.visibility = View.VISIBLE
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/

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
                    bindingCV.heartIcon.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    bindingCV.heartIcon.setImageResource(android.R.drawable.btn_star_big_off)
                }
            }
        }
    }
}