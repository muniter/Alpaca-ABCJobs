package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentTechnicalInfoCreateBinding
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.Validators
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel
import com.google.android.material.textfield.TextInputEditText


class TechnicalInfoCreateFragment : Fragment(),
    View.OnClickListener,
    TechnicalInfoCreateViewModel.NavigationListener {

    private val tokenLiveData = MutableLiveData<String?>()
    private var isValidTypeTechnicalItem: Boolean = false

    private lateinit var viewModel: TechnicalInfoCreateViewModel
    private lateinit var binding: FragmentTechnicalInfoCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = "Crear información técnica"

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_technical_info_create,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TechnicalInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[TechnicalInfoCreateViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val view = inflater.inflate(
            R.layout.fragment_technical_info_create,
            container,
            false
        )

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val btnSave: Button = view.findViewById(R.id.technicalInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.technicalInfoCancelButton)
        val spinnerTechnicalTypeItem = view.findViewById<AppCompatSpinner>(R.id.spinnerTechnicalItem)
        val labelTypeTechnicalItemError = view.findViewById<TextView>(R.id.labelTechnicalItemError)
        val editTextDescription = view.findViewById<TextInputEditText>(R.id.editTextDescriptionInfo)
        val labelDescriptionError = view.findViewById<TextView>(R.id.labelDescriptionInfoError)
        val selectedTypeTechnicalItem = spinnerTechnicalTypeItem.selectedItem?.toString() ?: ""

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.getTypesTechnicalItems()
        }

        viewModel.typesTechnicalInfoTypes.observe(viewLifecycleOwner) { typesTechnicalInfoTypes ->
            if (typesTechnicalInfoTypes != null) {
                val typesTechnicalInfoTypesNames = typesTechnicalInfoTypes.map { it.name }
                val adapter = context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        typesTechnicalInfoTypesNames
                    )
                }
                adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTechnicalTypeItem.adapter = adapter
            }
        }

        val editTextTechnicalType = convertStringToTextInputEditText(selectedTypeTechnicalItem)

        setupFieldValidation(
            editTextTechnicalType,
            labelTypeTechnicalItemError
        ) { text ->
            validateAndShowTypeTechnicalItemError(text, labelTypeTechnicalItemError)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TechnicalInfoCreateViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun convertStringToTextInputEditText(text: String): TextInputEditText {
        val editText = TextInputEditText(requireContext())
        editText.setText(text)
        return editText
    }
    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.action_technicalInfoCreateFragment_to_nav_technical_info)
    }
    private fun setupFieldValidation(
        editText: TextInputEditText,
        errorLabel: TextView,
        validationFunction: (String) -> Unit
    ) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newText = editText.text.toString()
                validationFunction(newText)
            }
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s.toString()
                validationFunction(newText)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                validationFunction(newText)
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                validationFunction(newText)
            }
        })
    }

    private fun validateAndShowTypeTechnicalItemError(text: String, labelError: TextView) {
        val isValid = Validators.isNotEmpty(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Se debe elegir al menos un tipo de conocmiiento técnico"
            isValidTypeTechnicalItem = false
            disableButton(view?.findViewById(R.id.technicalInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidTypeTechnicalItem = true
            enableButton(view?.findViewById(R.id.technicalInfoSaveButton)!!)
        }
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.technicalInfoSaveButton -> {
                val description = view?.findViewById<TextInputEditText>(R.id.editTextDescriptionInfo)?.text.toString()
                val typeTechnical = view?.findViewById<Spinner>(R.id.spinnerTechnicalItem)?.selectedItem.toString()

                val typeTechnicalItem = viewModel.getIdTypeTechnicalItem(typeTechnical)
                toggleControl(false)
                viewModel.saveTechnicalInfoItem(
                    TechnicalInfoRequest(
                        description,
                        typeTechnicalItem
                    ))
                view?.findNavController()?.navigate(R.id.action_technicalInfoCreateFragment_to_nav_technical_info)
            }
            R.id.technicalInfoCancelButton -> {
                view?.findNavController()?.navigate(R.id.action_technicalInfoCreateFragment_to_nav_technical_info)
            }
        }
    }

    private fun disableButton(button: Button) {
        button.isEnabled = false
    }

    private fun enableButton(button: Button) {
        if (isValidTypeTechnicalItem
        ) {
            button.isEnabled = true
        }
    }


    private fun toggleControl(estate: Boolean) {
        view?.findViewById<TextInputEditText>(R.id.editTextDescriptionInfo)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerTechnicalItem)?.isEnabled = estate
        view?.findViewById<Button>(R.id.technicalInfoSaveButton)?.isEnabled = estate
    }

}