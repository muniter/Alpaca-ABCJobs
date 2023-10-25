package com.example.abc_jobs_alpaca.viewmodel

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterModel
import com.google.android.material.textfield.TextInputEditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.model.models.UserDataResponse
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.utils.Validators

class CandidatoRegisterFragment : Fragment()
    , View.OnClickListener
    , CandidateRegisterModel.NavigationListener {

    private var isValidName: Boolean = false
    private var isValidLastName: Boolean = false
    private var isValidEmail: Boolean = false
    private var isValidPassword: Boolean = false
    private var isValidRePassword: Boolean = false
    private var isValidTerms: Boolean = false
    private lateinit var viewModel:  CandidateRegisterModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_candidato_register, container, false)
        val btn: Button = view.findViewById(R.id.button_register)
        btn.setOnClickListener(this)

        val viewModel = ViewModelProvider(this).get(CandidateRegisterModel::class.java)

        viewModel.getMessageLiveData().observe(viewLifecycleOwner) { messageEvent ->
            when (messageEvent.type) {
                MessageType.SUCCESS -> {
                    val userData = messageEvent.content as UserDataResponse
                    Log.d("TestTag", userData.candidato.email)
                    showToast(getString(R.string.toast_message_successful),
                        R.drawable.toast_success_background)
                }
                MessageType.ERROR -> {
                    when(messageEvent.content.toString()){
                        ""-> showToast(getString(R.string.toast_message_network_error),
                                        R.drawable.toast_error_background);
                        else -> showToast(getString(R.string.toast_message_registration_failed),
                                            R.drawable.toast_error_background)
                    }
                }
            }
        }

        val editTextName = view.findViewById<TextInputEditText>(R.id.editTextName)
        val labelNameError = view.findViewById<TextView>(R.id.labelNameError)
        val editTextLastName = view.findViewById<TextInputEditText>(R.id.editTextLastName)
        val labelLastNameError = view.findViewById<TextView>(R.id.labelLastNameError)
        val editTextEmail = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val labelEmailError = view.findViewById<TextView>(R.id.labelEmailError)
        val editTextPassword = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val labelPasswordError = view.findViewById<TextView>(R.id.labelPasswordError)
        val editTextRePassword = view.findViewById<TextInputEditText>(R.id.editTextRepeatPassword)
        val labelRePasswordError = view.findViewById<TextView>(R.id.labelRepeatPasswordError)
        val checkBoxTerms = view.findViewById<CheckBox>(R.id.checkBoxTerms)
        val labelTermsError = view.findViewById<TextView>(R.id.labelTermsError)
        val showPasswordButton = view.findViewById<ImageButton>(R.id.togglePasswordVisibility)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.editTextPassword)

        setupFieldValidation(editTextName, labelNameError) { newText ->
            validateAndShowNameError(newText, labelNameError)
        }

        setupFieldValidation(editTextLastName, labelLastNameError) { newText ->
            validateAndShowLastNameError(newText, labelLastNameError)
        }

        setupFieldValidation(editTextEmail, labelEmailError) { email ->
            validateEmail(email, labelEmailError)
        }

        //Password field

        setupFieldValidation(editTextPassword, labelPasswordError) { text ->
            validatePassword(text, labelPasswordError)
        }

        //Re-password field

        editTextRePassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = editTextPassword.text.toString().trim()
                val rePassword = editTextRePassword.text.toString().trim()
                validateRePassword(password, rePassword, labelRePasswordError)
            }
        }

        editTextRePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val password = editTextPassword.text.toString().trim()
                val rePassword = s.toString().trim()
                validateRePassword(password, rePassword, labelRePasswordError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = editTextPassword.text.toString().trim()
                val rePassword = s.toString().trim()
                validateRePassword(password, rePassword, labelRePasswordError)
            }

            override fun afterTextChanged(s: Editable?) {
                val password = editTextPassword.text.toString().trim()
                val rePassword = s.toString().trim()
                validateRePassword(password, rePassword, labelRePasswordError)
            }
        })

        //Terms and conditions checkbox
        checkBoxTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            handleTermsCheckBox(isChecked, labelTermsError)
        }

        //Show password
        showPasswordButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    passwordInput.inputType = InputType.TYPE_CLASS_TEXT
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                }
            }
            true
        }

        return view
    }


    private fun showToast(message: String, backgroundDrawableRes: Int) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.view?.setBackgroundResource(backgroundDrawableRes)
        toast.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CandidateRegisterModel::class.java)

        viewModel.getEnabledElementsLiveData().observe(viewLifecycleOwner, Observer { state ->
            toggleControl(state)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[CandidateRegisterModel::class.java]
        viewModel.setNavigationListener(this)
    }

    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.loginFragment)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_register -> {
                val name = view?.findViewById<TextInputEditText>(R.id.editTextName)?.text.toString()
                val lastName = view?.findViewById<TextInputEditText>(R.id.editTextLastName)?.text.toString()
                val email = view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.text.toString()
                val password = view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.text.toString()
                toggleControl(false);
                val candidate = UserRegisterRequest(
                    nombres = name,
                    apellidos = lastName,
                    email = email,
                    password = password,
                )

                viewModel.postCandidate(candidate)

                requireActivity().supportFragmentManager.popBackStack()

            }
        }

    }
private fun toggleControl(estate: Boolean){
    view?.findViewById<Button>(R.id.button_register)?.isEnabled = estate
    view?.findViewById<TextInputEditText>(R.id.editTextName)?.isEnabled = estate
    view?.findViewById<TextInputEditText>(R.id.editTextLastName)?.isEnabled = estate
    view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.isEnabled = estate
    view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.isEnabled = estate
    view?.findViewById<TextInputEditText>(R.id.editTextRepeatPassword)?.isEnabled = estate
    view?.findViewById<CheckBox>(R.id.checkBoxTerms)?.isEnabled = estate
}

    // Validation
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
    private fun validateAndShowNameError(text: String, labelError: TextView) {
        val isValid = Validators.validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.name_validation_error)
            isValidName = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidName = true
            enableButton(view?.findViewById(R.id.button_register)!!)
        }
    }
    private fun validateAndShowLastNameError(text: String, labelError: TextView) {
        val isValid = Validators.validateLastName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.lastnames_validation_error)
            isValidLastName = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidLastName = true
            enableButton(view?.findViewById(R.id.button_register)!!)
        }
    }
    private fun validateEmail(email: String, labelError: TextView) {
        if (email.isEmpty() || email.length < 5 || email.length > 255 || !Validators.isValidEmail(email)) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.email_validation_error)
            isValidEmail = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidEmail = true
        }
    }
    private fun validatePassword(password: String, labelError: TextView) {
        val isValid = Validators.isPasswordValid(password)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.password_validation_error)
            isValidPassword = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidPassword = true
            enableButton(view?.findViewById(R.id.button_register)!!)
        }
    }
    private fun validateRePassword(password: String, rePassword: String, labelError: TextView) {
        val isValid = Validators.areStringsEqual(password, rePassword)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.no_same_passwords)
            isValidRePassword = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidRePassword = true
            enableButton(view?.findViewById(R.id.button_register)!!)
        }
    }
    private fun handleTermsCheckBox(isChecked: Boolean, labelError: TextView) {
        if (!isChecked) {
            showValidationError(labelError, getString(R.string.term_cond_validation_error))
            isValidTerms = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            hideValidationError(labelError)
        }
    }
    private fun showValidationError(labelError: TextView, errorMessage: String) {
        labelError.visibility = View.VISIBLE
        labelError.text = errorMessage
    }
    private fun hideValidationError(labelError: TextView) {
        labelError.visibility = View.GONE
        labelError.text = ""
        isValidTerms = true
        enableButton(view?.findViewById(R.id.button_register)!!)
    }
    private fun disableButton(button: Button) {
        button?.isEnabled = false
    }
    private fun enableButton(button: Button) {
        if(isValidName &&
            isValidLastName &&
            isValidEmail &&
            isValidPassword &&
            isValidRePassword &&
            isValidTerms) {
            button?.isEnabled = true
        }
    }
}


