package com.example.abc_jobs_alpaca

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
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText


class CandidatoRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_candidato_register, container, false)


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

        //Password field
        val editTextPassword = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val labelPasswordError = view.findViewById<TextView>(R.id.labelPasswordError)
        editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = editTextPassword.text.toString().trim()
                validatePassword(password, labelPasswordError)
            }
        }

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

    private fun validateAndShowNameError(text: String, labelError: TextView) {
        if (text.isEmpty() || text.isBlank() || text.length < 2 || text.length > 100) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Debe tener entre 2 y 100 caracteres y no puede estar vacío."
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
        }
    }

    private fun validateAndShowLastNameError(text: String, labelError: TextView) {
        if (text.isEmpty() || text.isBlank() || text.length < 2 || text.length > 100) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Debe tener entre 2 y 100 caracteres y no puede estar vacío."
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
        }
    }

    private fun validateEmail(email: String, labelError: TextView) {
        if (email.isEmpty() || email.length < 5 || email.length > 255 || !isValidEmail(email)) {
            labelError.visibility = View.VISIBLE
            labelError.text = "El correo electrónico no es válido. Debe tener entre 5 y 255 caracteres y tener un formato válido (ej: nombre@dominio.com)."
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
        }
    }
    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun validatePassword(password: String, labelError: TextView) {
        if (password.isEmpty() || password.length < 8 || password.length > 255) {
            labelError.visibility = View.VISIBLE
            labelError.text = "La contraseña debe tener entre 8 y 20 caracteres."
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
        }
    }

    private fun validateRePassword(password: String, rePassword: String, labelError: TextView) {
        if (password != rePassword) {
            labelError.visibility = View.VISIBLE
            labelError.text = "Las contraseñas no coinciden."
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
        }
    }

    private fun handleTermsCheckBox(isChecked: Boolean, labelError: TextView) {
        if (!isChecked) {
            showValidationError(labelError, "Debe aceptar los términos y condiciones.")
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
    }

}