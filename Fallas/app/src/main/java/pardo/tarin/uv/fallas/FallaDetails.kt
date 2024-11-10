package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pardo.tarin.uv.fallas.databinding.FragmentFallaDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FallaDetails : Fragment() {

    private lateinit var binding: FragmentFallaDetailsBinding
    private lateinit var falla: Falla
    private var tipo: String = ""
    private var imgBtnFav: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        falla = arguments?.getSerializable("falla") as Falla
        Log.d("FallaDetails", falla.toString())
        /*try {
            falla = arguments?.getSerializable("adultas") as Falla
            tipo = "adultas"
        }
        catch (e: Exception)
        {
            falla = arguments?.getSerializable("infantiles") as Falla
            tipo = "infantiles"
        }*/
        //falla = arguments?.getSerializable("falla") as Falla

        binding = FragmentFallaDetailsBinding.inflate(inflater, container, false)

        binding.textView2.text = "\uD83C\uDFC6 ${getString(R.string.premios)} \uD83C\uDFC6"
        binding.textView6.text = getString(R.string.IGtitulo).uppercase()
        binding.seccionText.text = getString(R.string.seccion).uppercase()
        imgBtnFav = (activity as MainActivity).botonfav
        GlobalScope.launch {
            val esfavorito = comprobarFavorito(falla)
            Log.d("ComprobarFavorito", esfavorito.toString())
            withContext(Dispatchers.Main) {
                if (esfavorito) {
                    imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_on)
                } else {
                    imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_off)
                }
                imgBtnFav!!.visibility = View.VISIBLE
            }
        }

        (activity as MainActivity).botonfav.setOnClickListener {
            GlobalScope.launch {
                val esfavorito = comprobarFavorito(falla)
                withContext(Dispatchers.Main) {
                    if (esfavorito) {
                        imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_off)
                        //falla.favorito = false
                        borrarFavorito(falla)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.RemoveFav),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        imgBtnFav!!.setImageResource(android.R.drawable.btn_star_big_on)
                        //falla.favorito = true
                        añadirFavorito(falla)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.AddFav),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        }

        binding.mapaButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.mapaTitulo))
                .setMessage(getString(R.string.mapaMensaje))
                .setPositiveButton(getString(R.string.Si)) { _, _ ->
                    //val uri = Uri.parse("geo:0,0?q=${falla.coordenadas?.first},${falla.coordenadas?.second}(${falla.nombre})")
                    val uri = Uri.parse("geo:0,0?q=${falla.coordLat},${falla.coordLong}(${falla.nombre})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        binding.boceto.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.bocetoTitulo))
                .setMessage(getString(R.string.bocetoMensaje))
                .setPositiveButton(getString(R.string.Si)) { _, _ ->
                    val searchTerm = falla.boceto // reemplaza con el término de búsqueda que quieras
                    val uri = Uri.parse(searchTerm)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                .setNegativeButton("No", null)
                .show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d("FallaDetails", "${falla.boceto}")
        val imageview = binding.boceto
        Glide.with(this)
            .load("${falla.boceto}")
            .into(imageview)
        /*Glide.with(this)
            .load("${falla.escudo}")
            .into(binding.escudo)*/
        binding.nombreBoceto.text = falla.lema
        binding.nombreArtista.text = "${getString(R.string.artista)}: ${falla.artista}"
        /*if(tipo == "adultas")
        {
            binding.nombreFallera.text = "Fallera Mayor:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente:\n     ${falla.presidente}"
        }
        else
        {
            binding.nombreFallera.text = "Fallera Mayor Infantil:\n     ${falla.fallera}"
            binding.nombrePresidente.text = "Presidente Infantil:\n     ${falla.presidente}"
        }*/
        if(falla.seccion == "IE" || falla.seccion == "E")
        {
            binding.seccionText.text = getString(R.string.seccionEspecial).uppercase()
        }
        else if(falla.seccion == "FC")
        {
            binding.seccionText.text = getString(R.string.seccionFueraConcurso).uppercase()
            binding.premios.visibility = View.GONE
        }
        else
            binding.seccionText.text = "${getString(R.string.seccion)} ${falla.seccion}"

        if(falla.premio == "Sin premio" && falla.premioE == "Sin premio")
        {
            binding.premios.visibility = View.GONE
        }
        else {
            if (falla.premio != "Sin premio") {
                binding.numeroPremio.text = "${falla.premio}º"
            } else {
                binding.PremioSeccion.visibility = View.GONE
            }

            if (falla.premioE != "Sin premio") {
                binding.numeroIE.text = "${falla.premioE}º"
            } else {
                binding.PremioIE.visibility = View.GONE
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = falla.nombre
    }

    fun añadirFavorito(falla: Falla) {
        val dbfirestore = FirebaseFirestore.getInstance()
        /*val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()*/
        val falla = Falla(falla.objid, falla.id, falla.nombre, falla.escudo, falla.seccion, falla.premio, falla.premioE, falla.fallera, falla.presidente, falla.artista, falla.lema, falla.boceto, falla.experim, falla.coordLat, falla.coordLong)
        GlobalScope.launch {
            try{
                //fallaDao.insertFalla(falla)
                val fallaMap = falla.toMap()
                dbfirestore.collection("users").document(DataHolder.publicEmail).get().addOnSuccessListener {
                    val fallasFav = it.get("favoritas") as MutableList<Map<String, Any>>
                    fallasFav.add(fallaMap as Map<String, Any>)
                    dbfirestore.collection("users").document(DataHolder.publicEmail).update("favoritas", fallasFav)
                }
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al añadir el camping a favoritos: ${e.message}")
            }
        }
    }

    fun borrarFavorito(falla: Falla) {
        /*val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()*/
        val dbfirestore = FirebaseFirestore.getInstance()
        GlobalScope.launch {
            try{
                //fallaDao.delete(falla)
                Log.d("FirestoreBorrar", falla.objid.toString())
                dbfirestore.collection("users").document(DataHolder.publicEmail).get().addOnSuccessListener {
                    val fallasFav = it.get("favoritas") as MutableList<Map<String, Any>>
                    for (i in fallasFav){
                        if (i["objid"] == falla.objid)
                            Log.d("FirestoreBorrar", i["objid"].toString())
                            fallasFav.remove(i)
                    }
                    //fallasFav.removeIf { it.objid == falla.objid }
                    dbfirestore.collection("users").document(DataHolder.publicEmail).update("favoritas", fallasFav)
                }
            } catch (e: Exception) {
                Log.e("CampingDetails", "Error al eliminar el camping de favoritos: ${e.message}")
            }
        }
    }

    //fun comprobarFavorito(falla: Falla): Boolean {
        /*val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val fallaDao = db.fallaDao()
        GlobalScope.launch {
            try {
                val favorito = fallaDao.getFalla(falla.objid)
                withContext(Dispatchers.Main) {
                    callback(favorito != null)
                }
            } catch (e: Exception) {
                Log.e("FallaDetails", "Error al comprobar si la falla es favorita: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }
         */
    suspend fun comprobarFavorito(falla: Falla) : Boolean {
        val db = FirebaseFirestore.getInstance()
        var esFavorito = false
        try{
            val result = db.collection("users").document(DataHolder.publicEmail).get().await()
            val fallasFav = result.get("favoritas") as MutableList<Map<String, Any>>
            for (i in fallasFav){
                if (i["objid"].toString() == falla.objid.toString()){
                    esFavorito = true
                    break
                }
            }
        } catch (e: Exception) {
            Log.e("ComprobarFavorito", "Error al comprobar la falla de favoritos: ${e.message}")
        }

        return esFavorito
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).botonfav.visibility = View.GONE
    }
}