package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.viewmodel.PersonalInfoViewModel
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentPersonalInfoBinding
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalInfoFragment : Fragment(), View.OnClickListener    {

    companion object {
        fun newInstance() = PersonalInfoFragment()
    }

    private lateinit var viewModel: PersonalInfoViewModel
    private lateinit var binding : FragmentPersonalInfoBinding

    private val tokenLiveData = MutableLiveData<String?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("token", null)

        if(token == null){
            token = ""
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PersonalInfoViewModel(token, ABCJobsRepository(activity!!.application)) as T
            }
        })[PersonalInfoViewModel::class.java]

        binding.viewModel = viewModel

        tokenLiveData.value = token

        val view = binding.root

        val btn: Button = view.findViewById(R.id.button_save_personal_info)
        btn.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save_personal_info -> {
                val birthDate = null
                val countryCode = null
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

                lifecycleScope.launch(Dispatchers.Main){
                    var personalInfoRequest = PersonalInfoRequest(if (birthDate == "") null else birthDate,
                                                                  if (countryCode == "") null else countryCode,
                                                                  if (city == "") null else city,
                                                                  if (address == "") null else address,
                                                                  if (phone == "") null else phone,
                                                                  if (biography == "") null else biography,
                                                                  languages)
                    viewModel.savePersonalInfo(personalInfoRequest)
                }

            }
        }
    }

}