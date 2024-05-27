package pardo.tarin.uv.fallas

import android.os.Bundle
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
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getData(){
        val linearLayout = binding.layoutFallasFavs
        val inflater = LayoutInflater.from(context)
        CoroutineScope(Dispatchers.IO).launch {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "fallasFavoritas"
            ).build()
            val userDao = db.fallaDao()
            val listaFavoritas = userDao.getAll()
            println("Lista de favoritos: $listaFavoritas")
            if(listaFavoritas.isEmpty()){
                binding.listaFavVacia.visibility = View.VISIBLE
            } else {
                binding.listaFavVacia.visibility = View.INVISIBLE
            }
            withContext(Dispatchers.Main) {
                fallasData = ArrayList(listaFavoritas)
                /*myAdapter = FavouriteAdapter(campingsData, object: FavouriteAdapter.CampingFavoriteItemListener {
                    override fun onItemClick(camping: Camping) {
                        val bundle = Bundle()
                        bundle.putSerializable("camping", camping)
                        findNavController().navigate(R.id.action_campingFavouriteList_to_campingDetails2, bundle)
                    }
                })
                binding.rvFav.adapter = myAdapter*/
                val fallaView = inflater.inflate(R.layout.falla_view, linearLayout, false)

                for(j in fallasData) {
                    val falla = j
                    val nombre = fallaView.findViewById<TextView>(R.id.falla_name)
                    nombre.text = falla.nombre
                    val seccion = fallaView.findViewById<TextView>(R.id.falla_section)
                    seccion.text = falla.seccion
                    val premio = fallaView.findViewById<TextView>(R.id.falla_prize)
                    val medalla = fallaView.findViewById<TextView>(R.id.falla_medalla)
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
                    linearLayout.addView(fallaView)
                    fallaView.setOnClickListener() {
                        if (isAdded) {
                            val bundle = Bundle()
                            bundle.putSerializable("falla", falla)
                            findNavController().navigate(
                                R.id.action_fallasFragment_to_fallaDetails2,
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