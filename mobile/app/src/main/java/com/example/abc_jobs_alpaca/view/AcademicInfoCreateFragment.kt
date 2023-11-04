package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.Layout.Directions
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentAcademicInfoCreateBinding
import com.example.abc_jobs_alpaca.model.models.AcademicInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.Validators
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoCreateViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AcademicInfoCreateFragment : Fragment(),
    View.OnClickListener,
    AcademicInfoCreateViewModel.NavigationListener{

    private val tokenLiveData = MutableLiveData<String?>()
    private var isValidNameInstitution: Boolean = false
    private var isValidTypeDegree: Boolean = false
    private var isValidNameDegree: Boolean = false
    private var isValidDateInit: Boolean = false
    private lateinit var binding: FragmentAcademicInfoCreateBinding
    private lateinit var viewModel: AcademicInfoCreateViewModel
    private var isFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = "Crear formación académica"

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_academic_info_create,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[AcademicInfoCreateViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val view = inflater.inflate(R.layout.fragment_academic_info_create, container, false)
        val btnSave : Button = view.findViewById(R.id.academicInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.academicInfoCancelButton)
        val checkFinishedStudies : CheckBox = view.findViewById(R.id.checkBoxCompletedStudies)
        val endDateLayout : TextInputLayout = view.findViewById(R.id.textInputLayoutEndDate)
        val editTextNameInstitution = view.findViewById<TextInputEditText>(R.id.editTextInstitution)
        val editTextNameDegree = view.findViewById<TextInputEditText>(R.id.editTextDegree)
        val labelNameInstitutionError = view.findViewById<TextView>(R.id.labelInstitutionError)
        val labelNameDegreeError = view.findViewById<TextView>(R.id.labelDegreeError)
        val labelTypeDegreeError = view.findViewById<TextView>(R.id.labelEducationLevelError)
        val labelStartDateError = view.findViewById<TextView>(R.id.labelStartDateError)
        val labelEndDateError = view.findViewById<TextView>(R.id.labelEndDateError)
        endDateLayout.visibility = View.GONE

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val spinnerEducationLevel = view.findViewById<AppCompatSpinner>(R.id.spinnerEducationLevel)
        val spinnerStartYear = view.findViewById<AppCompatSpinner>(R.id.spinnerStartDate)
        val spinnerEndYear = view.findViewById<AppCompatSpinner>(R.id.spinnerEndDate)
        val educationLevelSelected = spinnerEducationLevel.selectedItem?.toString() ?: ""
        val startYearSelected = spinnerStartYear.selectedItem?.toString()?.toInt() ?: 0
        val endYearSelected = spinnerEndYear.selectedItem?.toString()?.toInt() ?: 0

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { it ->
            viewModel.onTokenUpdated(it)
            viewModel.getTypesDegree()
        }

        viewModel.typesTitles.observe(viewLifecycleOwner) { types ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEducationLevel.adapter = adapter
        }

        val yearsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.getYears()
        )
        if(yearsAdapter != null){
            yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStartYear.adapter = yearsAdapter
            spinnerEndYear.adapter = yearsAdapter
        }

        checkFinishedStudies.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                endDateLayout.visibility = View.VISIBLE
                isFinished = true
            }else{
                endDateLayout.visibility = View.GONE
                isFinished = false
            }
        }

        val editTextEducationLevel = convertStringToTextInputEditText(educationLevelSelected)
        val editTextStartYear = convertStringToTextInputEditText(startYearSelected.toString())
        val editTextEndYear = convertStringToTextInputEditText(endYearSelected.toString())

        setupFieldValidation(editTextNameInstitution, labelNameInstitutionError ) { newText ->
            validateAndShowNameInstitutionError(newText, labelNameInstitutionError)
        }
        setupFieldValidation(editTextNameDegree, labelNameDegreeError ) { newText ->
            validateAndShowNameDegree(newText, labelNameDegreeError)
        }

        setupFieldValidation(editTextEducationLevel, labelTypeDegreeError ) { newText ->
            validateAndShowTypeDegreeError(newText, labelTypeDegreeError)
        }
        setupFieldValidation(editTextStartYear, labelStartDateError ) { newText ->
            validateAndShowDateError(newText?.toInt(), endYearSelected, labelStartDateError)
        }

        if(isFinished){
            setupFieldValidation(editTextEndYear, labelEndDateError ) { newText ->
                validateAndShowDateError(startYearSelected, newText?.toInt(), labelEndDateError)
            }
        }

        spinnerStartYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validateAndShowDateError(spinnerStartYear.selectedItem.toString().toInt(), spinnerEndYear.selectedItem.toString().toInt(),
                    labelEndDateError)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejo para cuando no se selecciona nada.
            }
        }

        spinnerEndYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    validateAndShowDateError(spinnerStartYear.selectedItem.toString().toInt(), spinnerEndYear.selectedItem.toString().toInt(),
                        labelEndDateError)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO: Manejo para cuando no se selecciona nada. Aunque viene un valor por defecto.
            }
        }

        return view
    }

    override  fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[AcademicInfoCreateViewModel::class.java]
        viewModel.getEnabledElementsLiveData().observe(viewLifecycleOwner, Observer { state ->
            toggleControl(state)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[AcademicInfoCreateViewModel::class.java]
        viewModel.setNavigationListener(this)
    }

     private fun convertStringToTextInputEditText(text: String): TextInputEditText {
         val editText = TextInputEditText(requireContext())
         editText.setText(text)
         return editText
     }

    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.action_academicInfoCreateFragment_to_nav_academic_info)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.academicInfoSaveButton -> {
                val institution = view?.findViewById<EditText>(R.id.editTextInstitution)?.text.toString()
                val title = view?.findViewById<EditText>(R.id.editTextDegree)?.text.toString()

                val startYear =
                    view?.findViewById<Spinner>(R.id.spinnerStartDate)?.selectedItem.toString().toInt()

                val achievement =
                    view?.findViewById<EditText>(R.id.editTextAdditionalInfo)?.text.toString()

                val degreeTypeSelected =
                    view?.findViewById<Spinner>(R.id.spinnerEducationLevel)?.selectedItem.toString()
                val typeDegree = viewModel.getIdTypeDegree(degreeTypeSelected)

                val endYear: Int = if(isFinished) {
                    view?.findViewById<Spinner>(R.id.spinnerEndDate)?.selectedItem.toString()
                        .toInt()
                }else 0

                toggleControl(false)
                viewModel.saveAcademicInfoItem(
                    AcademicInfoRequest(institution, title, startYear, endYear, achievement, typeDegree)
                )
                view?.findNavController()?.navigate(R.id.action_academicInfoCreateFragment_to_nav_academic_info)

            }
            R.id.academicInfoCancelButton -> {
                view?.findNavController()?.navigate(R.id.action_academicInfoCreateFragment_to_nav_academic_info)
            }
        }
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

    private fun validateAndShowNameInstitutionError(text: String, labelError: TextView) {
        val isValid = Validators.validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidNameInstitution = false
            disableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidNameInstitution = true
            enableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        }
    }

    private fun validateAndShowNameDegree(text: String, labelError: TextView) {
        val isValid = Validators.validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidNameDegree = false
            disableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidNameDegree = true
            enableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        }
    }

    private fun validateAndShowTypeDegreeError(text: String, labelError: TextView) {
        val isValid = Validators.isNotEmpty(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidTypeDegree = false
            disableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidTypeDegree = true
            enableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        }
    }

    private fun validateAndShowDateError(first: Int?, second: Int?, labelError: TextView) {
        val isValid = Validators.isNotEmpty(first.toString())
        var isValidDate: Boolean
        if(isFinished){
            isValidDate = Validators.compareTwoNumbers(first, second)
        }else{
            isValidDate = true
        }

        if (!isValid || !isValidDate) {
            labelError.visibility = View.VISIBLE
            labelError.text = "La fecha de inicio debe ser menor a la fecha de fin"
            isValidDateInit = false
            disableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidDateInit = true
            enableButton(view?.findViewById(R.id.academicInfoSaveButton)!!)
        }
    }
    private fun disableButton(button: Button) {
        button.isEnabled = false
    }

    private fun enableButton(button: Button) {
        if (isValidNameInstitution
        ) {
            button.isEnabled = true
        }
    }

    private fun toggleControl(estate: Boolean) {
        view?.findViewById<Button>(R.id.academicInfoSaveButton)?.isEnabled = estate
        view?.findViewById<Button>(R.id.academicInfoCancelButton)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextInstitution)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextDegree)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextAdditionalInfo)?.isEnabled = estate
        view?.findViewById<CheckBox>(R.id.checkBoxCompletedStudies)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerEducationLevel)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerStartDate)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerEndDate)?.isEnabled = estate
    }
}