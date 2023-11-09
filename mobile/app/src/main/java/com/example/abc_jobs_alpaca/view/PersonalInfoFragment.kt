package com.example.abc_jobs_alpaca.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentPersonalInfoBinding
import com.example.abc_jobs_alpaca.model.models.Country
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.Validators
import com.example.abc_jobs_alpaca.viewmodel.PersonalInfoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PersonalInfoFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = PersonalInfoFragment()
    }

    private var isValidBirthDate: Boolean = true
    private var isValidCity: Boolean = true
    private var isValidAddress: Boolean = true
    private var isValidPhone: Boolean = true
    private var isValidBio: Boolean = true
    private var isAgeless: Boolean = false


    private lateinit var viewModel: PersonalInfoViewModel
    private lateinit var binding: FragmentPersonalInfoBinding

    private val tokenLiveData = MutableLiveData<String?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("token", null) ?: ""
        var dateFormat = sharedPreferences.getString("dateFormat", "dd/MM/yyyy") ?: ""

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PersonalInfoViewModel(token, dateFormat, ABCJobsRepository(activity!!.application)) as T
            }
        })[PersonalInfoViewModel::class.java]

        binding.viewModel = viewModel

        tokenLiveData.value = token

        val view = binding.root

        val saveBtn: Button = view.findViewById(R.id.button_save_personal_info)
        saveBtn.setOnClickListener(this)
        val cancelBtn: Button = view.findViewById(R.id.button_cancel_personal_info)
        cancelBtn.setOnClickListener(this)
        val addBtn: FloatingActionButton = view.findViewById(R.id.addOrEditButton)
        addBtn.setOnClickListener(this)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        editTextDate.setOnClickListener(this)

        val spinner: Spinner = view.findViewById(R.id.countrySpinner)
        spinner.onItemSelectedListener=this

        viewModel.personalInfo.observe(viewLifecycleOwner, Observer {
            if(it?.country_code != null){
                var index = viewModel.countrys.value?.indexOfFirst { country -> country?.num_code == it.country_code }

                if(index != null && index >= 0){
                    spinner.setSelection(index)
                }
            }
        })

        setValidations(view)

        (activity as MainActivity).hideButton();

        return view
    }

    private fun showDatePicker() {

        var year = viewModel.personalInfo.value?.birth_date?.year ?: 0
        var month = viewModel.personalInfo.value?.birth_date?.monthValue ?: 0
        var day = viewModel.personalInfo.value?.birth_date?.dayOfMonth ?: 1

        val dpd = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            val localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)

            viewModel.personalInfo.value?.birth_date = localDate
            if(ChronoUnit.YEARS.between(localDate, LocalDate.now()) < 18) {
                val labelBirthDateError = requireView()
                    .findViewById<TextView>(R.id.labelBirthDateError)

                this.isAgeless = true
                validateAndShowBirthDateError("", labelBirthDateError)
            } else {
                this.isAgeless = false
            }

            viewModel.updateDateString()

        }, year, month-1, day)

        dpd.show()
    }

    // Validation
    private fun setValidations(view: View){
        val editBirthDateName = view.findViewById<EditText>(R.id.editTextDate)
        val labelBirthDateError = view.findViewById<TextView>(R.id.labelBirthDateError)

        setupFieldValidation(editBirthDateName, labelBirthDateError) { newText ->
            validateAndShowBirthDateError(newText, labelBirthDateError)
        }

        val editCityName = view.findViewById<TextInputEditText>(R.id.editTextCity)
        val labelCityError = view.findViewById<TextView>(R.id.labelCityError)

        setupFieldValidation(editCityName, labelCityError) { newText ->
            validateAndShowCityError(newText, labelCityError)
        }

        val editAddressName = view.findViewById<TextInputEditText>(R.id.editTextAddress)
        val labelAddressError = view.findViewById<TextView>(R.id.labelAddressError)

        setupFieldValidation(editAddressName, labelAddressError) { newText ->
            validateAndShowAddressError(newText, labelAddressError)
        }

        val editPhoneName = view.findViewById<TextInputEditText>(R.id.editTextPhone)
        val labelPhoneError = view.findViewById<TextView>(R.id.labelPhoneError)

        setupFieldValidation(editPhoneName, labelPhoneError) { newText ->
            validateAndShowPhoneError(newText, labelPhoneError)
        }

        val editBioName = view.findViewById<TextInputEditText>(R.id.editTextBio)
        val labelBioError = view.findViewById<TextView>(R.id.labelBioError)

        setupFieldValidation(editBioName, labelBioError) { newText ->
            validateAndShowBioError(newText, labelBioError)
        }
    }

    private fun setupFieldValidation(
        editText: EditText,
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save_personal_info -> {
                val birthDate = viewModel.personalInfo.value?.birth_date
                var birthDateString: String? = null

                // Format the selected date into a string

                if (birthDate != null) {
                    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = dateFormat.format(birthDate)

                    birthDateString = formattedDate
                }

                val countryCode =
                    (view?.findViewById<Spinner>(R.id.countrySpinner)?.selectedItem as Country?)?.num_code
                val city =
                    view?.findViewById<TextInputEditText>(R.id.editTextCity)?.text?.toString()
                val address =
                    view?.findViewById<TextInputEditText>(R.id.editTextAddress)?.text?.toString()
                val phone =
                    view?.findViewById<TextInputEditText>(R.id.editTextPhone)?.text?.toString()
                val biography =
                    view?.findViewById<TextInputEditText>(R.id.editTextBio)?.text?.toString()
                val languages = null
                //toggleControl(false)

                lifecycleScope.launch(Dispatchers.Main) {
                    var personalInfoRequest = PersonalInfoRequest(
                        if (birthDateString == "") null else birthDateString,
                        countryCode,
                        if (city == "") null else city,
                        if (address == "") null else address,
                        if (phone == "") null else phone,
                        if (biography == "") null else biography,
                        languages
                    )
                    viewModel.savePersonalInfo(personalInfoRequest)
                }

                viewModel.enableForm.postValue(false)
            }

            R.id.editTextDate -> {
                showDatePicker()
            }

            R.id.button_cancel_personal_info -> {
                viewModel.enableForm.postValue(false)
            }

            R.id.addOrEditButton -> {
                viewModel.enableForm.postValue(true)
                viewModel.showForm.postValue(true)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent!!.setSelection(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun validateAndShowBirthDateError(text: String, labelError: TextView) {
        val isValid = Validators.isValidBirthDate(text)

        if (!isValid || isAgeless) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.birthdate_validation_error)
            isValidCity = false
            disableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidCity = true
            enableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        }
    }

    private fun validateAndShowCityError(text: String, labelError: TextView) {
        val isValid = Validators.isValidCity(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.city_validation_error)
            isValidCity = false
            disableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidCity = true
            enableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        }
    }

    private fun validateAndShowAddressError(text: String, labelError: TextView) {
        val isValid = Validators.isValidAddress(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.address_validation_error)
            isValidAddress = false
            disableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidAddress = true
            enableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        }
    }

    private fun validateAndShowPhoneError(text: String, labelError: TextView) {
        val isValid = Validators.isValidPhone(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.phone_validation_error)
            isValidPhone = false
            disableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidPhone = true
            enableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        }
    }

    private fun validateAndShowBioError(text: String, labelError: TextView) {
        val isValid = Validators.isValidBio(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.bio_validation_error)
            isValidBio = false
            disableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidBio = true
            enableButton(view?.findViewById(R.id.button_save_personal_info)!!)
        }
    }

    private fun disableButton(button: Button) {
        button.isEnabled = false
    }

    private fun enableButton(button: Button) {
        if (isValidBirthDate &&
            isValidCity &&
            isValidAddress &&
            isValidPhone &&
            isValidBio &&
            !isAgeless
        ) {
            button.isEnabled = true
        }
    }
}
