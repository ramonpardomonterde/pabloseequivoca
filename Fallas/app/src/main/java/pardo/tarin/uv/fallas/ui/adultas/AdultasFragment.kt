package pardo.tarin.uv.fallas.ui.adultas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pardo.tarin.uv.fallas.databinding.FragmentAdultasBinding

class AdultasFragment : Fragment() {

    private var _binding: FragmentAdultasBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val adultasViewModel =
            ViewModelProvider(this).get(AdultasViewModel::class.java)*/

        _binding = FragmentAdultasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        textView.text = "This is adultas Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}