package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pardo.tarin.uv.fallas.bdRoom.AppDatabase
import pardo.tarin.uv.fallas.databinding.FragmentFavoritosBinding

class FavoritosFragment : Fragment() {
    private lateinit var binding: FragmentFavoritosBinding
    private lateinit var fallasData: ArrayList<Falla>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.favoritos)
        getData()

        binding.borrarTodosFav.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.borrarTitulo))
                .setMessage(getString(R.string.borrarMensaje))
                .setPositiveButton(getString(R.string.Si)) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = Room.databaseBuilder(
                            requireContext(),
                            AppDatabase::class.java, "fallasFavoritas"
                        ).fallbackToDestructiveMigration().build()
                        val userDao = db.fallaDao()
                        userDao.deleteAll()
                        fallasData.clear()
                        withContext(Dispatchers.Main) {
                            binding.layoutFallasFavs.removeAllViews()
                            binding.listaFavVacia.visibility = View.VISIBLE
                            binding.borrarTodosFav.visibility = View.GONE
                            requireActivity().invalidateOptionsMenu()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        return binding.root
    }

    private fun getData(){
        val linearLayout = binding.layoutFallasFavs
        val inflater = LayoutInflater.from(context)
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "fallasFavoritas"
            ).fallbackToDestructiveMigration().build()
            val userDao = db.fallaDao()
            val listaFavoritas = userDao.getAll()

            withContext(Dispatchers.Main) {
                if(listaFavoritas.isEmpty()){
                    binding.listaFavVacia.visibility = View.VISIBLE
                    binding.borrarTodosFav.visibility = View.GONE
                } else {
                    binding.listaFavVacia.visibility = View.GONE
                    binding.borrarTodosFav.visibility = View.VISIBLE
                }
                fallasData = ArrayList(listaFavoritas)

                for(j in fallasData) {
                    val falla = j
                    // Inflate a new fallaView for each iteration of the loop
                    val fallaView = inflater.inflate(R.layout.falla_view, linearLayout, false)

                    val nombre = fallaView.findViewById<TextView>(R.id.falla_name)
                    nombre.text = falla.nombre
                    val seccion = fallaView.findViewById<TextView>(R.id.falla_section)
                    seccion.visibility = View.VISIBLE
                    if (falla.seccion == "IE") {
                        seccion.text = getString(R.string.seccionEspecialInfantl)
                    } else if(falla.seccion == "E"){
                        seccion.text = getString(R.string.seccionEspecial)
                    } else {
                        seccion.text = getString(R.string.seccion) + " " + falla.seccion
                    }
                    val premio = fallaView.findViewById<TextView>(R.id.falla_prize)
                    val medalla = fallaView.findViewById<TextView>(R.id.falla_medalla)
                    premio.text = "${getString(R.string.premioSeccion)}: ${falla.premio}"
                    medalla.text = ""
                    premio.visibility = View.VISIBLE
                    if(falla.premioE != "Sin premio") {
                        val premioE = fallaView.findViewById<TextView>(R.id.fallaEG_prize)
                        premioE.visibility = View.VISIBLE
                        premioE.text = "${getString(R.string.premioIG)}: ${falla.premioE}"
                    }
                    linearLayout.addView(fallaView)
                    fallaView.setOnClickListener() {
                        if (isAdded) {
                            val bundle = Bundle()
                            bundle.putSerializable("falla", falla)
                            findNavController().navigate(
                                R.id.action_favoritosFragment_to_fallaDetails,
                                bundle
                            )
                        }
                    }
                }
                requireActivity().invalidateOptionsMenu()
            }
        }
    }
}