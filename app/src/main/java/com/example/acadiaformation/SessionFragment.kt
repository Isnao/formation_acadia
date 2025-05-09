package isnao.acadiaformation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.util.forEach
import androidx.core.util.isEmpty
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import isnao.acadiaformation.databinding.FragmentSessionBinding
import isnao.acadiaformation.databinding.SessionComportementsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class SessionFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentSessionBinding? = null
    private lateinit var popupView: SessionComportementsBinding
    private lateinit var popupWindow: PopupWindow
    private var strArray: Array<String> = MainActivity.Niveau.niveau1
    private val sessionViewModel: SessionViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (sessionViewModel.listToString() == "") {
            val file =
                File(
                    activity?.applicationContext?.filesDir ?: binding.root.context.filesDir,
                    "formation_acadia.csv",
                )
            if (file.exists()) {
                sessionViewModel.import(file.inputStream())
                file.inputStream().close()
            }
        }

        setPopupWindow()

        binding.niveauSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (position == 2) {
                        strArray = MainActivity.Niveau.niveau3
                    } else if (position == 1) {
                        strArray = MainActivity.Niveau.niveau2
                    } else {
                        strArray = MainActivity.Niveau.niveau1
                    }
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(popupView.root.context, android.R.layout.simple_list_item_multiple_choice, strArray)
                    initList(popupView.listComportements.checkedItemPositions)
                    popupView.listComportements.adapter = adapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

        binding.saveButton.setOnClickListener { _ ->
            writeLocally(
                binding.dateText.text.toString(),
                binding.objectifInput.text.toString(),
                binding.remarquesInput.text
                    .toString()
                    .replace("\n", ". "),
                binding.dureeInput.text.toString(),
                binding.validatedSwitch.isChecked,
            )
            Snackbar
                .make(binding.root.rootView, "Temps de formation enregistré", Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAnchorView(R.id.fab)
                .show()

            binding.objectifInput.setText(R.string.blank)
            binding.remarquesInput.setText(R.string.blank)
            binding.dureeInput.setText(R.string.blank)
            binding.validatedSwitch.isChecked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeLocally(
        date: String,
        objectif: String,
        remarques: String,
        duree: String,
        validation: Boolean,
    ) {
        sessionViewModel.addSession(SessionItem(date, getComportements(), objectif, remarques, duree, validation))
        val file = File(activity?.applicationContext?.filesDir ?: binding.root.context.filesDir, "formation_acadia.csv")
        file.writeText("Date;Comportement;Objectif;Remarques;Duree;Session Validee ?\n")
        file.appendText(sessionViewModel.listToString())
    }

    @SuppressLint("SimpleDateFormat")
    private fun setPopupWindow() {
        popupView = SessionComportementsBinding.inflate(layoutInflater)
        popupView.listComportements.adapter =
            ArrayAdapter<String>(popupView.root.context, android.R.layout.simple_list_item_multiple_choice, MainActivity.Niveau.niveau1)
        popupView.okButton.setOnClickListener { _ ->
            Snackbar
                .make(binding.root.rootView, "Comportements choisi : ${getComportements()}", Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAnchorView(R.id.fab)
                .show()
            popupWindow.dismiss()
        }
        popupView.allButton.setOnClickListener { _ ->
            popupView.listComportements.checkedItemPositions.forEach { key, _ ->
                popupView.listComportements.checkedItemPositions[key] = true
            }
            popupWindow.dismiss()
        }
        popupView.cancelButton.setOnClickListener { _ ->
            popupView.listComportements.checkedItemPositions.clear()
            popupWindow.dismiss()
        }
        popupWindow = PopupWindow(popupView.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)

        popupWindow.setOnDismissListener {
            binding.comportementText.setText(getComportements())
        }
        binding.comportementText.setOnClickListener { _ ->
            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
            if (popupView.listComportements.checkedItemPositions.isEmpty()) {
                initList(popupView.listComportements.checkedItemPositions)
            }
        }

        val datePicker =
            MaterialDatePicker.Builder
                .datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        binding.dateText
            .setText(
                SimpleDateFormat("dd/MM/yyyy")
                    .format(Date(MaterialDatePicker.todayInUtcMilliseconds())),
            )
        datePicker.addOnPositiveButtonClickListener { value ->
            binding.dateText
                .setText(SimpleDateFormat("dd/MM/yyyy").format(Date(value)))
        }
        binding.dateText.setOnClickListener { _ ->
            datePicker.show(parentFragmentManager, "tag")
        }
    }

    private fun getComportements(): String {
        var comportement: String = ""
        var allChecked = true
        popupView.listComportements.checkedItemPositions.forEach { key, value ->
            if (value) {
                if (comportement != "") {
                    comportement += "/"
                }
                comportement += strArray[key]
            } else {
                allChecked = false
            }
        }
        if (allChecked) {
            return binding.niveauSpinner.selectedItem.toString()
        }
        return comportement
    }

    private fun initList(list: SparseBooleanArray) {
        var index = 0
        list.clear()
        strArray.forEach {
            list.set(index++, value = false)
        }
        binding.comportementText.setText(getComportements())
    }
}
