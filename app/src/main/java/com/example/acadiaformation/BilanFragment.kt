package isnao.acadiaformation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import isnao.acadiaformation.databinding.BilanTabItemBinding
import isnao.acadiaformation.databinding.FragmentBilanBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class BilanFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentBilanBinding? = null

    private val binding get() = _binding!!
    private val sessionViewModel: SessionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBilanBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        getLocal()

        resources.getStringArray(R.array.niveaux).forEach { elem ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(elem))
        }

        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        if (tab.text == resources.getStringArray(R.array.niveaux)[1]) {
                            MainActivity.Niveau.niveau2.forEach { elem ->
                                binding.tabSubLayout.addTab(binding.tabSubLayout.newTab().setText(elem))
                            }
                        } else if (tab.text == resources.getStringArray(R.array.niveaux)[2]) {
                            MainActivity.Niveau.niveau3.forEach { elem ->
                                binding.tabSubLayout.addTab(binding.tabSubLayout.newTab().setText(elem))
                            }
                        } else {
                            MainActivity.Niveau.niveau1.forEach { elem ->
                                binding.tabSubLayout.addTab(binding.tabSubLayout.newTab().setText(elem))
                            }
                        }
                    } else {
                        MainActivity.Niveau.niveau1.forEach { elem ->
                            binding.tabSubLayout.addTab(binding.tabSubLayout.newTab().setText(elem))
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    onTabSelected(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    binding.tabSubLayout.removeAllTabs()
                    binding.tabContent.removeAllViews()
                }
            },
        )

        binding.tabSubLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val correctLine =
                        sessionViewModel.progress.find { elem ->
                            elem.contains(tab?.text)
                        }
                    val bilanBinding = BilanTabItemBinding.inflate(layoutInflater)

                    bilanBinding.checkBox1.isChecked = (correctLine?.get(1) ?: "") != ""
                    bilanBinding.editTextDate1.setText(correctLine?.get(1) ?: "")
                    if (bilanBinding.checkBox1.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate1)
                    }
                    bilanBinding.checkBox2.isChecked = (correctLine?.get(2) ?: "") != ""
                    bilanBinding.editTextDate2.setText(correctLine?.get(2) ?: "")
                    if (bilanBinding.checkBox2.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate2)
                    }
                    bilanBinding.checkBox3.isChecked = (correctLine?.get(3) ?: "") != ""
                    bilanBinding.editTextDate3.setText(correctLine?.get(3) ?: "")
                    if (bilanBinding.checkBox3.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate3)
                    }
                    bilanBinding.checkBox4.isChecked = (correctLine?.get(4) ?: "") != ""
                    bilanBinding.editTextDate4.setText(correctLine?.get(4) ?: "")
                    if (bilanBinding.checkBox4.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate4)
                    }
                    bilanBinding.checkBox5.isChecked = (correctLine?.get(5) ?: "") != ""
                    bilanBinding.editTextDate5.setText(correctLine?.get(5) ?: "")
                    if (bilanBinding.checkBox5.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate5)
                    }
                    bilanBinding.checkBox6.isChecked = (correctLine?.get(6) ?: "") != ""
                    bilanBinding.editTextDate6.setText(correctLine?.get(6) ?: "")
                    if (bilanBinding.checkBox6.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate6)
                    }
                    bilanBinding.checkBox7.isChecked = (correctLine?.get(7) ?: "") != ""
                    bilanBinding.editTextDate7.setText(correctLine?.get(7) ?: "")
                    if (bilanBinding.checkBox7.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate7)
                    }
                    bilanBinding.checkBox8.isChecked = (correctLine?.get(8) ?: "") != ""
                    bilanBinding.editTextDate8.setText(correctLine?.get(8) ?: "")
                    if (bilanBinding.checkBox8.isChecked) {
                        checkedOnClick(bilanBinding.editTextDate8)
                    }

                    bilanBinding.editTextDate1.doAfterTextChanged { text ->
                        correctLine?.set(1, text.toString())
                    }
                    bilanBinding.editTextDate2.doAfterTextChanged { text ->
                        correctLine?.set(2, text.toString())
                    }
                    bilanBinding.editTextDate3.doAfterTextChanged { text ->
                        correctLine?.set(3, text.toString())
                    }
                    bilanBinding.editTextDate4.doAfterTextChanged { text ->
                        correctLine?.set(4, text.toString())
                    }
                    bilanBinding.editTextDate5.doAfterTextChanged { text ->
                        correctLine?.set(5, text.toString())
                    }
                    bilanBinding.editTextDate6.doAfterTextChanged { text ->
                        correctLine?.set(6, text.toString())
                    }
                    bilanBinding.editTextDate7.doAfterTextChanged { text ->
                        correctLine?.set(7, text.toString())
                    }
                    bilanBinding.editTextDate8.doAfterTextChanged { text ->
                        correctLine?.set(8, text.toString())
                    }
                    bilanBinding.checkBox1.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate1)
                    }
                    bilanBinding.checkBox2.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate2)
                    }
                    bilanBinding.checkBox3.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate3)
                    }
                    bilanBinding.checkBox4.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate4)
                    }
                    bilanBinding.checkBox5.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate5)
                    }
                    bilanBinding.checkBox6.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate6)
                    }
                    bilanBinding.checkBox7.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate7)
                    }
                    bilanBinding.checkBox8.setOnCheckedChangeListener { _, isChecked ->
                        checkboxListener(isChecked, bilanBinding.editTextDate8)
                    }
                    bilanBinding.button2.setOnClickListener {
                        writeLocally()
                    }
                    binding.tabContent.addView(bilanBinding.root)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    onTabSelected(tab)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    binding.tabContent.removeAllViews()
                }
            },
        )

        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkboxListener(
        isChecked: Boolean,
        editTextDate: TextInputEditText,
    ) {
        if (isChecked) {
            checkedOnClick(editTextDate)
        } else {
            editTextDate.setText("")
            editTextDate.setOnClickListener { }
        }
    }

    private fun checkedOnClick(editTextDate: TextInputEditText) {
        var selection = MaterialDatePicker.todayInUtcMilliseconds()

        if (!editTextDate.text.isNullOrEmpty()) {
            var tempDate = editTextDate.text.toString().split("/")
            if (tempDate.size == 1) {
                tempDate = editTextDate.text.toString().split("-")
            }

            val calendar =
                android.icu.util.Calendar
                    .getInstance()
            calendar.set(tempDate[2].toInt(), tempDate[1].toInt() - 1, tempDate[0].toInt())
            selection = calendar.timeInMillis
        }

        val datePicker =
            MaterialDatePicker.Builder
                .datePicker()
                .setSelection(selection)
                .build()

        editTextDate
            .setText(
                SimpleDateFormat("dd/MM/yyyy")
                    .format(Date(selection)),
            )

        datePicker.addOnPositiveButtonClickListener { value ->
            editTextDate
                .setText(SimpleDateFormat("dd/MM/yyyy").format(Date(value)))
        }
        editTextDate.setOnClickListener { _ ->
            datePicker.show(parentFragmentManager, "tag")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeLocally() {
        val file = File(activity?.applicationContext?.filesDir ?: binding.root.context.filesDir, "bilan_progres.csv")
        file.writeText(";Capture/Façonnage;Signal;Durée;Position;Distance;Distraction;Généralisation;Maîtrise\n")
        sessionViewModel.progress.forEach { lines ->
            file.appendText("${lines.joinToString(";")}\n")
        }
    }

    private fun getLocal() {
        val file = File(activity?.applicationContext?.filesDir ?: binding.root.context.filesDir, "bilan_progres.csv")
        if (!file.exists()) {
            val list = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
            val ressourceArray =
                MainActivity.Niveau.niveau1
                    .plus(MainActivity.Niveau.niveau2)
                    .plus(MainActivity.Niveau.niveau3)
            sessionViewModel.progress =
                Array<Array<String>>(
                    22,
                    fun (index: Int): Array<String> =
                        Array<String>(
                            9,
                            fun (subIndex: Int): String =
                                if (subIndex == 0) {
                                    ressourceArray[index]
                                } else {
                                    ""
                                },
                        ),
                )
            list.forEachIndexed { index, _ ->
                sessionViewModel.progress[index] = arrayOf(ressourceArray[index], "", "", "", "", "", "", "", "")
            }
            writeLocally()
        } else {
            val lines = file.readText().split("\n")

            sessionViewModel.progress =
                Array<Array<String>>(21, fun (_: Int): Array<String> = Array<String>(9, fun (_: Int): String = ""))
            lines.forEachIndexed { index, s ->
                if (index != 0 && index <= 21) {
                    sessionViewModel.progress[index - 1] = s.split(';').toTypedArray()
                }
            }
        }
    }
}
