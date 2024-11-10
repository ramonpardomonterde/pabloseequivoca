package pardo.tarin.uv.fallas.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.TranslateAnimation
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import pardo.tarin.uv.fallas.DataHolder
import pardo.tarin.uv.fallas.LoginActivity
import pardo.tarin.uv.fallas.MainActivity
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var displayMetrics: android.util.DisplayMetrics? = null
    var screenWidth : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        displayMetrics = context?.resources?.displayMetrics
        screenWidth = displayMetrics?.widthPixels
        //val email = arguments?.getString("email")
        binding.nombreusuario.text = DataHolder.publicEmail

        // Calcula el 70% del ancho de la pantalla
        val maxWidth = (screenWidth!! * 0.7).toInt()

        // Establece el ancho máximo de popUpText
        binding.popUpText.maxWidth = maxWidth

        val packageInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
        val version = packageInfo?.versionName

        binding.popUpText.text = "${getString(R.string.creador)}: Pablo Tarín\n${getString(R.string.version)}: $version\n\n\n${getString(R.string.description)}"
        binding.PopUp.visibility = View.GONE


        val infButton = binding.monumentosInfButton
        var scaledDrawable = scaleDrawable(R.drawable.pareja_falleros_infantil, 100, 100)
        infButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)
        infButton.setText(getString(R.string.menu_infantiles))

        infButton.setOnClickListener {
            //val action = HomeFragmentDirections.actionNavHomeToInfantilesFragment("infantiles")
            findNavController().navigate(R.id.action_nav_home_to_infantilesFragment)
        }

        val adultosButton = binding.monumentosAdultosButton
        scaledDrawable = scaleDrawable(R.drawable.falla_dibujo_boton, 90, 127)
        adultosButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)
        adultosButton.setText(getString(R.string.menu_adultas))

        adultosButton.setOnClickListener {
            //val action = HomeFragmentDirections.actionNavHomeToFallasFragment("adultas")
            findNavController().navigate(R.id.action_nav_home_to_adultasFragment)
        }

        val mapaButton = binding.mapaButton
        scaledDrawable = scaleDrawable(R.drawable.mapa, 80, 80)
        mapaButton.setCompoundDrawablesWithIntrinsicBounds(null, scaledDrawable, null, null)
        mapaButton.setText(getString(R.string.home_mapa))

        mapaButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_mapaFragment)
        }

        val eventosButton = binding.eventosButton
        scaledDrawable = scaleDrawable(R.drawable.calendario, 80, 80)
        eventosButton.setCompoundDrawablesWithIntrinsicBounds(null, scaledDrawable, null, null)
        eventosButton.setText(getString(R.string.home_calendario))

        eventosButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_eventsFragment)
        }

        val favButton = binding.favButton
        scaledDrawable = scaleDrawable(R.drawable.corazon, 30, 30)
        favButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, scaledDrawable, null)
        favButton.setText(getString(R.string.favoritos))

        favButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_favoritosFragment)
        }

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        scaledDrawable = scaleDrawable(R.drawable.logout, 20, 20)
        binding.cerrarSesionButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)

        binding.cerrarSesionButton.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle(getString(R.string.cerrarSesion))
            builder.setMessage(getString(R.string.consultarCerrarSesion))
            builder.setPositiveButton(getString(R.string.Si)) { dialog, _ ->
                dialog.dismiss()
                cerrarSesion(DataHolder.publicEmail)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        binding.textView7.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_favoritosFragment)
        }

        binding.AcercaDe.text = getString(R.string.acercaDe)

        binding.AcercaDe.setOnClickListener(){
            binding.popUpText.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Asegúrate de eliminar el oyente para evitar múltiples llamadas
                    binding.popUpText.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    // Ahora puedes obtener el ancho de popUpText y establecer el ancho de view
                    val layoutParams = binding.view.layoutParams
                    layoutParams.width = binding.popUpText.width + 100
                    layoutParams.height = binding.popUpText.height + 100
                    binding.view.layoutParams = layoutParams
                }
            })
            binding.PopUp.visibility = View.VISIBLE
        }

        binding.closePopUp.setOnClickListener(){
            binding.PopUp.visibility = View.GONE
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        // Obtén una referencia a botonfav
        val botonfav = (activity as MainActivity).botonfav
        botonfav.visibility = View.VISIBLE

        var abierto = false
        // Cambia la imagen de botonfav
        botonfav.setImageResource(R.drawable.usuario50)
        botonfav.setOnClickListener {
            if (!abierto) {
                botonfav.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                botonfav.setImageResource(R.drawable.usuario50)
            }
            AbrirCerrarMenu(abierto)
            abierto = !abierto
        }

        binding.nombreusuario.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Asegúrate de eliminar el oyente para evitar múltiples llamadas
                binding.nombreusuario.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Ahora puedes obtener el ancho de los elementos
                Log.d("AnchoNombre", binding.nombreusuario.width.toString())

                if(binding.nombreusuario.width > screenWidth!!) {
                    binding.nombreusuario.width = screenWidth!! / 2
                    binding.cerrarSesionButton.width = screenWidth!! / 2
                } else {
                    if(binding.nombreusuario.width > binding.cerrarSesionButton.width) {
                        binding.cerrarSesionButton.width = binding.nombreusuario.width
                        Log.d("Ancho", binding.cerrarSesionButton.width.toString())
                    } else {
                        binding.nombreusuario.width = binding.cerrarSesionButton.width
                        Log.d("AnchoBoton", binding.nombreusuario.width.toString())
                    }
                }
            }
        })

        binding.nombreusuario.text = arguments?.getString("email")
    }

    fun cerrarSesion(email: String) {
        //añadirFavoritasUsuario(email)
        val prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()

        // Inicia LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)

        // Cierra la actividad actual
        activity?.finish()
    }

    /*private fun añadirFavoritasUsuario(email: String){
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "fallasFavoritas"
        ).fallbackToDestructiveMigration().build()
        val userDao = db.fallaDao()
        val listaFavoritas = userDao.getAll()

        val dbfirestore = FirebaseFirestore.getInstance()
        dbfirestore.collection("usuarios").document(email).set(hashMapOf("favoritas" to listaFavoritas))
    }*/

    fun AbrirCerrarMenu(abierto: Boolean) {
        val opcionesUsuario: View = binding.opcionesUsuario
        // Calcula la distancia a moverse
        val distance = opcionesUsuario.width

        val animation: TranslateAnimation
        var translationX = 0f

        if(abierto) {
            animation = TranslateAnimation(-distance.toFloat(), 0f, 0f, 0f)
            translationX = 0f
        } else {
            animation = TranslateAnimation(0f, -distance.toFloat(), 0f, 0f)
            translationX = -distance.toFloat()
        }

        //animation.duration = 500 // Duración de la animación en milisegundos

        // Aplica la animación a la vista
        //opcionesUsuario.startAnimation(animation)

        opcionesUsuario.translationX = translationX
    }

    override fun onPause() {
        super.onPause()

        // Obtén una referencia a botonfav
        val botonfav = (activity as MainActivity).botonfav

        // Haz que botonfav desaparezca
        botonfav.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun scaleDrawable(drawableId: Int, width: Int, height: Int): Drawable {
        val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDrawable(resources, scaledBitmap)
    }
}