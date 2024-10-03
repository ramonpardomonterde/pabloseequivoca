package pardo.tarin.uv.fallas.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import pardo.tarin.uv.fallas.LoginActivity
import pardo.tarin.uv.fallas.MainActivity
import pardo.tarin.uv.fallas.R
import pardo.tarin.uv.fallas.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val email = arguments?.getString("email")
        binding.textView7.text = email

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
            abierto = !abierto

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