package com.example.abc_jobs_alpaca

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.abc_jobs_alpaca.model.models.Candidate
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterModel
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.utils.Validators

class CandidatoRegisterFragment : Fragment(), View.OnClickListener {

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
        // Name field
        val editTextName = view.findViewById<TextInputEditText>(R.id.editTextName)
        val labelNameError = view.findViewById<TextView>(R.id.labelNameError)
        editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newText = editTextName.text.toString()
                validateAndShowNameError(newText, labelNameError)
            }
        }
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s.toString()
                validateAndShowNameError(newText, labelNameError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                validateAndShowNameError(newText, labelNameError)
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                validateAndShowNameError(newText, labelNameError)
            }
        })

        // Last name field
        val editTextLastName = view.findViewById<TextInputEditText>(R.id.editTextLastName)
        val labelLastNameError = view.findViewById<TextView>(R.id.labelLastNameError)
        editTextLastName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newText = editTextLastName.text.toString()
                validateAndShowLastNameError(newText, labelLastNameError)
            }
        }
        editTextLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s.toString()
                validateAndShowLastNameError(newText, labelLastNameError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                validateAndShowLastNameError(newText, labelLastNameError)
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                validateAndShowLastNameError(newText, labelLastNameError)
            }
        })

        // Email field
        val editTextEmail = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val labelEmailError = view.findViewById<TextView>(R.id.labelEmailError)
        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = editTextEmail.text.toString().trim()
                validateEmail(email, labelEmailError)
            }
        }
        editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val email = s.toString().trim()
                validateEmail(email, labelEmailError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                validateEmail(email, labelEmailError)
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                validateEmail(email, labelEmailError)
            }
        })

        //Password field
        val editTextPassword = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val labelPasswordError = view.findViewById<TextView>(R.id.labelPasswordError)
        editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = editTextPassword.text.toString().trim()
                validatePassword(password, labelPasswordError)
            }
        }

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val password = s.toString().trim()
                validatePassword(password, labelPasswordError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString().trim()
                validatePassword(password, labelPasswordError)
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                validatePassword(password, labelPasswordError)
            }
        })

        //Re-password field
        val editTextRePassword = view.findViewById<TextInputEditText>(R.id.editTextRepeatPassword)
        val labelRePasswordError = view.findViewById<TextView>(R.id.labelRepeatPasswordError)
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
        val checkBoxTerms = view.findViewById<CheckBox>(R.id.checkBoxTerms)
        val labelTermsError = view.findViewById<TextView>(R.id.labelTermsError)
        checkBoxTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            handleTermsCheckBox(isChecked, labelTermsError)
        }

        //Show password
        val showPasswordButton = view.findViewById<ImageButton>(R.id.togglePasswordVisibility)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.editTextPassword)

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[CandidateRegisterModel::class.java]
    }

    private fun validateAndShowNameError(text: String, labelError: TextView) {
        val isValid = Validators().validateName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Debe tener entre 2 y 100 caracteres y no puede estar vacío."
            isValidName = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidName = true
            enableButton(view?.findViewById(R.id.button_register)!!)
        }
    }


    // Uso de la función
    private fun validateAndShowLastNameError(text: String, labelError: TextView) {
        val isValid = Validators().validateLastName(text)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Debe tener entre 2 y 100 caracteres y no puede estar vacío."
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
        if (email.isEmpty() || email.length < 5 || email.length > 255 || !Validators().isValidEmail(email)) {
            labelError.visibility = View.VISIBLE
            labelError.text = "El correo electrónico no es válido. (ej: nombre@dominio.com)."
            isValidEmail = false
            disableButton(view?.findViewById(R.id.button_register)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidEmail = true
        }
    }

    private fun validatePassword(password: String, labelError: TextView) {
        val isValid = Validators().isPasswordValid(password)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "La contraseña debe tener entre 8 y 255 caracteres."
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
        val isValid = Validators().areStringsEqual(password, rePassword)

        if (!isValid) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Las contraseñas no coinciden."
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
            showValidationError(labelError, "Debe aceptar los términos y condiciones.")
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



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_register -> {
                val name = view?.findViewById<TextInputEditText>(R.id.editTextName)?.text.toString()
                val lastName = view?.findViewById<TextInputEditText>(R.id.editTextLastName)?.text.toString()
                val email = view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.text.toString()
                val password = view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.text.toString()

                view?.findViewById<Button>(R.id.button_register)?.isEnabled = false
                view?.findViewById<TextInputEditText>(R.id.editTextName)?.isEnabled = false
                view?.findViewById<TextInputEditText>(R.id.editTextLastName)?.isEnabled = false
                view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.isEnabled = false
                view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.isEnabled = false
                view?.findViewById<TextInputEditText>(R.id.editTextRepeatPassword)?.isEnabled = false
                view?.findViewById<CheckBox>(R.id.checkBoxTerms)?.isEnabled = false

                val candidate = Candidate(
                    0,
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




}