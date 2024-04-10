package pardo.tarin.uv.fallas.ui.infantiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pardo.tarin.uv.fallas.databinding.FragmentInfantilesBinding

class InfantilesFragment : Fragment() {

    private var _binding: FragmentInfantilesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val infantilesViewModel =
            ViewModelProvider(this).get(InfantilesViewModel::class.java)*/

        _binding = FragmentInfantilesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        textView.text = "This is infantiles Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}