package pardo.tarin.uv.es

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.es.base_datos.AppDatabase
import pardo.tarin.uv.es.databinding.FragmentCampingFavouriteListBinding

class CampingFavouriteList : Fragment() {
    private lateinit var myAdapter: FavouriteAdapter
    private lateinit var binding: FragmentCampingFavouriteListBinding
    private var nombre = 0
    private lateinit var campingsData: ArrayList<Camping>
    private var optionsMenu: Menu? = null
    private var areButtonsVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCampingFavouriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indicar que este fragmento manejará los eventos del menú de opciones
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_favoritos, menu) // Inflar el menú de opciones
        optionsMenu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        comprobarListaVacia { isEmpty ->
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = !isEmpty
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortButton = view.findViewById<FloatingActionButton>(R.id.sortButton_favorite)
        val sortByName = view.findViewById<FrameLayout>(R.id.frameLayout_favorite)
        val sortByRating = view.findViewById<FloatingActionButton>(R.id.sortByRating_favorite)

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
                binding.sortButtonFavorite.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                binding.sortButtonFavorite.setImageResource(android.R.drawable.ic_menu_sort_by_size)
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
                binding.sortByNameTextFavorite.text = "Z->A"
                nombre = 1
            } else {
                ordenarDescendente("nombre")
                criterio = "Nombre (Z - A)"
                binding.sortByNameTextFavorite.text = "A->Z"
                nombre = 0
            }
            mostrarOrdenadoPor(criterio)
        }

        sortByRating.setOnClickListener {
            ordenarDescendente("categoria")
            mostrarOrdenadoPor("Categoria")
        }

        binding.rvFav.layoutManager = LinearLayoutManager(activity)

        val searchView = view.findViewById<SearchView>(R.id.searchView_favorite)
        searchView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        searchView.queryHint = "Buscar"
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.setOnCloseListener {
            searchView.setQuery("", false)
            true
        }
        val ordenadopor = view.findViewById<TextView>(R.id.ordenadopor_favorite)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    myAdapter.actualizarDatos(campingsData)
                } else {
                    val filteredData = ArrayList(campingsData.filter { camping ->
                        camping.nombre.contains(newText, ignoreCase = true)
                    })
                    myAdapter.actualizarDatos(filteredData)
                }
                if (newText.isNullOrEmpty()) {
                    ordenadopor.visibility = View.INVISIBLE
                }
                return false
            }
        })
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "campings_database"
            ).build()

            val userDao = db.campingDao()

            val listaFavoritos = userDao.getAll()
            println("Lista de favoritos: $listaFavoritos")
            if(listaFavoritos.isEmpty()){
                binding.listaFavVacia.visibility = View.VISIBLE
                binding.searchViewFavorite.visibility = View.INVISIBLE
                binding.ordenadoporFavorite.visibility = View.INVISIBLE
            } else {
                binding.listaFavVacia.visibility = View.INVISIBLE
                binding.searchViewFavorite.visibility = View.VISIBLE
            }

            withContext(Dispatchers.Main) {
                campingsData = ArrayList(listaFavoritos)
                myAdapter = FavouriteAdapter(campingsData, object: FavouriteAdapter.CampingFavoriteItemListener {
                    override fun onItemClick(camping: Camping) {
                        val bundle = Bundle()
                        bundle.putSerializable("camping", camping)
                        findNavController().navigate(R.id.action_campingFavouriteList_to_campingDetails2, bundle)
                    }
                })
                binding.rvFav.adapter = myAdapter
                requireActivity().invalidateOptionsMenu()
            }
        }
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
            R.id.borrar_favoritos -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Borrar favoritos")
                    .setMessage("¿Estás seguro de que quieres borrar todos los campings favoritos?")
                    .setPositiveButton("Sí") { _, _ ->
                        borrarFavoritos()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            android.R.id.home -> {
                findNavController().navigateUp() // Navegar hacia atrás
                return true
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
        binding.ordenadoporFavorite.visibility = View.VISIBLE
        binding.ordenadoporFavorite.text = "Ordenado por: $criterio"
    }

    private fun comprobarListaVacia(callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "campings_database"
            ).build()
            val campingDao = db.campingDao()

            val isEmpty = campingDao.getAll().isEmpty()

            withContext(Dispatchers.Main) {
                callback(isEmpty)
            }
        }
    }

    private fun borrarFavoritos() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "campings_database"
            ).build()

            val userDao = db.campingDao()

            userDao.deleteAll()

            withContext(Dispatchers.Main) {
                binding.listaFavVacia.visibility = View.VISIBLE
                binding.searchViewFavorite.visibility = View.INVISIBLE
                binding.ordenadoporFavorite.visibility = View.INVISIBLE

                campingsData.clear()
                myAdapter.actualizarDatos(campingsData)
                requireActivity().invalidateOptionsMenu()
            }
        }
    }
}