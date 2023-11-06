package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentWorkInfoCreateBinding
import com.example.abc_jobs_alpaca.model.models.WorkInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.Validators
import com.example.abc_jobs_alpaca.viewmodel.WorkInfoCreateViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class WorkInfoCreateFragment : Fragment(),
    View.OnClickListener,
    WorkInfoCreateViewModel.NavigationListener
{

    private val tokenLiveData = MutableLiveData<String?>()
    private var isValidNameCompany: Boolean = false
    private var isValidTypeSkill: Boolean = false
    private var isValidNameRole: Boolean = false
    private var isValidDateInit: Boolean = false
    private lateinit var binding: FragmentWorkInfoCreateBinding
    private lateinit var viewModel: WorkInfoCreateViewModel
    private var isFinished = false
    private var selectedOptions = ArrayList<Int>()



    companion object {
        fun newInstance() = WorkInfoCreateFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = "Crear información laboral"

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_work_info_create,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[WorkInfoCreateViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val view = inflater.inflate(R.layout.fragment_work_info_create, container, false)
        val btnSave : Button = view.findViewById(R.id.workInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.workInfoCancelButton)
        val checkFinishedStudies : CheckBox = view.findViewById(R.id.checkBoxCompletedJob)
        val endDateLayout : TextInputLayout = view.findViewById(R.id.textInputLayoutEndDateC)
        val editTextNameCompany = view.findViewById<TextInputEditText>(R.id.editTextCompany)
        val editTextNameRole = view.findViewById<TextInputEditText>(R.id.editTextRole)
        val labelNameCompanyError = view.findViewById<TextView>(R.id.labelCompanyError)
        val labelNameRoleError = view.findViewById<TextView>(R.id.labelRoleError)
        val labelTypeSkillError = view.findViewById<TextView>(R.id.labelSkillError)
        val labelStartDateCError = view.findViewById<TextView>(R.id.labelStartDateCError)
        val labelEndDateCError = view.findViewById<TextView>(R.id.labelEndDateCError)
        endDateLayout.visibility = View.GONE

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val spinnerSkill = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val spinnerStartYearC = view.findViewById<AppCompatSpinner>(R.id.spinnerStartDateC)
        val spinnerEndYearC = view.findViewById<AppCompatSpinner>(R.id.spinnerEndDateC)
        val startYearSelected = spinnerStartYearC.selectedItem?.toString()?.toInt() ?: 0
        val endYearSelected = spinnerEndYearC.selectedItem?.toString()?.toInt() ?: 0

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { it ->
            viewModel.onTokenUpdated(it)
            viewModel.getTypesSkills()
        }

        viewModel.typesSkills.observe(viewLifecycleOwner) { types ->
            val adapter = types?.let {
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    it.map { it.name }
                )
            }
            if (adapter != null) {
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinnerSkill.setAdapter(adapter)
        }


        val autoComplete  = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val chipGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroup)

        autoComplete.setOnItemClickListener { _, _, position, _ ->
        val selectedOption = viewModel.getIdTypeSkill(autoComplete.adapter.getItem(position).toString())
        val chip = Chip(requireContext())
        chip.text = autoComplete.adapter.getItem(position).toString()
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            }
            chipGroup.addView(chip)
            autoComplete.text = null
            selectedOptions.add(selectedOption.toInt())
        }

        val yearsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.getYears()
        )
        if(yearsAdapter != null){
            yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStartYearC.adapter = yearsAdapter
            spinnerEndYearC.adapter = yearsAdapter
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

        val editTextStartYear = convertStringToTextInputEditText(startYearSelected.toString())
        val editTextEndYear = convertStringToTextInputEditText(endYearSelected.toString())

        setupFieldValidation(editTextNameCompany, labelNameCompanyError ) { newText ->
            validateAndShowNameCompanyError(newText, labelNameCompanyError)
        }
        setupFieldValidation(editTextNameRole, labelNameRoleError ) { newText ->
            validateAndShowNameRole(newText, labelNameRoleError)
        }

        setupFieldValidation(editTextStartYear, labelStartDateCError ) { newText ->
            validateAndShowDateError(newText?.toInt(), endYearSelected, labelStartDateCError)
        }

        if(isFinished){
            setupFieldValidation(editTextEndYear, labelEndDateCError ) { newText ->
                validateAndShowDateError(startYearSelected, newText?.toInt(), labelEndDateCError)
            }
        }

        spinnerStartYearC.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validateAndShowDateError(spinnerStartYearC.selectedItem.toString().toInt(), spinnerEndYearC.selectedItem.toString().toInt(),
                    labelEndDateCError)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO: (Siempre hay un dato) Manejo para cuando no se selecciona nada. Aunque viene un valor por defecto.
            }
        }

        spinnerEndYearC.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validateAndShowDateError(spinnerStartYearC.selectedItem.toString().toInt(), spinnerEndYearC.selectedItem.toString().toInt(),
                    labelEndDateCError)
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
                return WorkInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[WorkInfoCreateViewModel::class.java]
        viewModel.getEnabledElementsLiveData().observe(viewLifecycleOwner, Observer { state ->
            toggleControl(state)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[WorkInfoCreateViewModel::class.java]
        viewModel.setNavigationListener(this)
    }

    private fun convertStringToTextInputEditText(text: String): TextInputEditText {
        val editText = TextInputEditText(requireContext())
        editText.setText(text)
        return editText
    }

    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.action_workInfoCreateFragment_to_nav_work_info)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.workInfoSaveButton -> {
                val company = view?.findViewById<EditText>(R.id.editTextCompany)?.text.toString()
                val role = view?.findViewById<EditText>(R.id.editTextRole)?.text.toString()

                val startYear =
                    view?.findViewById<Spinner>(R.id.spinnerStartDateC)?.selectedItem.toString().toInt()

                val description =
                    view?.findViewById<EditText>(R.id.editTextDescriptionWork)?.text.toString()

                val endYear: Int = if(isFinished) {
                    view?.findViewById<Spinner>(R.id.spinnerEndDateC)?.selectedItem.toString().toInt()
                }else 0


                toggleControl(false)
                viewModel.saveWorkInfoItem(
                    WorkInfoRequest(
                        role,
                        company,
                        description,
                        selectedOptions,
                        startYear,
                        endYear
                ))
                view?.findNavController()?.navigate(R.id.action_workInfoCreateFragment_to_nav_work_info)

            }
            R.id.workInfoCancelButton -> {
                view?.findNavController()?.navigate(R.id.action_workInfoCreateFragment_to_nav_work_info)
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

    private fun validateAndShowNameCompanyError(text: String, labelError: TextView) {
        val isValid = Validators.validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidNameCompany = false
            disableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidNameCompany = true
            enableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        }
    }

    private fun validateAndShowNameRole(text: String, labelError: TextView) {
        val isValid = Validators.validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidNameRole = false
            disableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidNameRole = true
            enableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        }
    }

    private fun validateAndShowTypeSkillError(text: String, labelError: TextView) {
        val isValid = Validators.isNotEmpty(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Se debe elegir un tipo de formación"
            isValidTypeSkill = false
            disableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidTypeSkill = true
            enableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
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
            disableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidDateInit = true
            enableButton(view?.findViewById(R.id.workInfoSaveButton)!!)
        }
    }
    private fun disableButton(button: Button) {
        button.isEnabled = false
    }

    private fun enableButton(button: Button) {
        if (isValidNameCompany
            && isValidNameRole
            && isValidDateInit
        ) {
            button.isEnabled = true
        }
    }

    private fun toggleControl(estate: Boolean) {
        view?.findViewById<Button>(R.id.workInfoSaveButton)?.isEnabled = estate
        view?.findViewById<Button>(R.id.workInfoCancelButton)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextCompany)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextRole)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextDescriptionWork)?.isEnabled = estate
        view?.findViewById<CheckBox>(R.id.checkBoxCompletedJob)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerStartDateC)?.isEnabled = estate
        view?.findViewById<Spinner>(R.id.spinnerEndDateC)?.isEnabled = estate
    }
}