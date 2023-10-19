package com.example.abc_jobs_alpaca

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.abc_jobs_alpaca.databinding.FragmentLoginBinding
import com.example.abc_jobs_alpaca.viewmodel.LoginMoldel
import com.google.android.material.textfield.TextInputEditText
import com.example.abc_jobs_alpaca.utils.Validators

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(){
    private var param1: String? = null
    private var param2: String? = null
    private var isValidEmail: Boolean = false
    private var isValidPassword: Boolean = false
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginMoldel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Infla el diseño utilizando Data Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this).get(LoginMoldel::class.java)

        // Asigna el ViewModel a la vista
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val view = binding.root;

        // Email field
        val editTextEmail = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val labelEmailError = view.findViewById<TextView>(R.id.labelEmailError)
        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = editTextEmail.text.toString()
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


    private fun validateEmail(email: String, labelError: TextView) {
        if (email.isEmpty() || email.length < 5 || email.length > 255 || !Validators().isValidEmail(email)) {
            labelError.visibility = View.VISIBLE
            labelError.text = "El correo electrónico no es válido. (ej: nombre@dominio.com)."
            isValidEmail = false
            disableButton(view?.findViewById(R.id.button_login)!!)
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
            disableButton(view?.findViewById(R.id.button_login)!!)
        } else {
            labelError.visibility = View.GONE
            labelError.text = ""
            isValidPassword = true
            enableButton(view?.findViewById(R.id.button_login)!!)
        }
    }

    private fun disableButton(button: Button) {
        button?.isEnabled = false
    }

    private fun enableButton(button: Button) {
        if(
            isValidEmail &&
            isValidPassword ) {
            button?.isEnabled = true
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}