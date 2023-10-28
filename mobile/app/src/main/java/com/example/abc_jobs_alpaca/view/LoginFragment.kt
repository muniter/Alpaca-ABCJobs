package com.example.abc_jobs_alpaca.view

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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentLoginBinding
import com.example.abc_jobs_alpaca.model.models.UserData
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.viewmodel.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import com.example.abc_jobs_alpaca.utils.Validators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(),View.OnClickListener, LoginViewModel.NavigationListener {
    private var param1: String? = null
    private var param2: String? = null

    private var isValidEmail: Boolean = false
    private var isValidPassword: Boolean = false
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.button_login -> {
                val email =
                    view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.text.toString()
                val password =
                    view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.text.toString()
                toggleControl(false)

                lifecycleScope.launch(Dispatchers.Main){
                    viewModel.login(email, password)
                }

                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(activity!!.application, ABCJobsRepository(activity!!.application)) as T
            }
        })[LoginViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val view = binding.root;

        val btn: Button = view.findViewById(R.id.button_login)
        btn.setOnClickListener(this)

        viewModel.getMessageLiveData().observe(viewLifecycleOwner) { messageEvent ->
            when (messageEvent.type) {
                MessageType.SUCCESS -> {
                    val userData = messageEvent.content as UserData
                    Log.d("TestTag", userData.usuario.email)
                    showToast(getString(R.string.succesful_message_login),
                        R.drawable.toast_success_background)
                }
                MessageType.ERROR -> {
                    when(messageEvent.content.toString()){
                        ""-> showToast(getString(R.string.toast_message_network_error),
                            R.drawable.toast_error_background);
                        else -> showToast(getString(R.string.toast_message_login_failed),
                            R.drawable.toast_error_background)
                    }
                }
            }
        }

        val editTextEmail = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val labelEmailError = view.findViewById<TextView>(R.id.labelEmailError)
        val editTextPassword = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val labelPasswordError = view.findViewById<TextView>(R.id.labelPasswordError)
        val showPasswordButton = view.findViewById<ImageButton>(R.id.togglePasswordVisibility)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.button_login)

        loginButton.isEnabled = false

        // Email field
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setNavigationListener(this)

        viewModel.getEnabledElementsLiveData().observe(viewLifecycleOwner, Observer { state ->
            toggleControl(state)
        })
    }

    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.mainActivity)
    }
    private fun validateEmail(email: String, labelError: TextView) {
        if (email.isEmpty() || email.length < 5 || email.length > 255 || !Validators.isValidEmail(email)) {
            labelError.visibility = View.VISIBLE
            labelError.text = getString(R.string.email_validation_error);
            isValidEmail = false
            disableButton(view?.findViewById(R.id.button_login)!!)
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

    private fun showToast(message: String, backgroundDrawableRes: Int) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.view?.setBackgroundResource(backgroundDrawableRes)
        toast.show()
    }

    private fun toggleControl(estate: Boolean){
        view?.findViewById<Button>(R.id.button_login)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextEmail)?.isEnabled = estate
        view?.findViewById<TextInputEditText>(R.id.editTextPassword)?.isEnabled = estate
    }

}