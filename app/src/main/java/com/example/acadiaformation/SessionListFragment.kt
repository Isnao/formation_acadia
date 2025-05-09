package isnao.acadiaformation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import isnao.acadiaformation.databinding.FragmentSessionListBinding
import java.io.File

class SessionListFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentSessionListBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSessionListBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        var text = ""
        val file = File(activity?.applicationContext?.filesDir, "formation_acadia.csv")

        text = file.readText()

        binding.textView.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
