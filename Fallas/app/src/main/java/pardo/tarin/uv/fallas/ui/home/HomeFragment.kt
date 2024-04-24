package pardo.tarin.uv.fallas.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        val infButton = binding.monumentosInfButton
        var scaledDrawable = scaleDrawable(R.drawable.pareja_falleros_infantil, 100, 100)
        infButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)
        infButton.setText("MONUMENTOS INFANTILES")

        infButton.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToFallasFragment("infantiles")
            findNavController().navigate(action)
        }

        val adultosButton = binding.monumentosAdultosButton
        scaledDrawable = scaleDrawable(R.drawable.falla_dibujo_boton, 90, 127)
        adultosButton.setCompoundDrawablesWithIntrinsicBounds(scaledDrawable, null, null, null)
        adultosButton.setText("MONUMENTOS ADULTOS")

        adultosButton.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToFallasFragment("adultas")
            findNavController().navigate(action)
        }

        val mapaButton = binding.mapaButton
        scaledDrawable = scaleDrawable(R.drawable.mapa, 80, 80)
        mapaButton.setCompoundDrawablesWithIntrinsicBounds(null, scaledDrawable, null, null)
        mapaButton.setText("MAPA")

        mapaButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_mapaFragment)
        }

        val eventosButton = binding.eventosButton
        scaledDrawable = scaleDrawable(R.drawable.calendario, 80, 80)
        eventosButton.setCompoundDrawablesWithIntrinsicBounds(null, scaledDrawable, null, null)
        eventosButton.setText("EVENTOS")

        eventosButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_calendarioFragment)
        }

        /*val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
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