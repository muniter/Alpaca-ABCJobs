package com.example.abc_jobs_alpaca.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentPreferencesBinding
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.DateUtils.dateFormatted
import com.example.abc_jobs_alpaca.utils.DateUtils.timeFormatted
import com.example.abc_jobs_alpaca.viewmodel.PreferencesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PreferencesFragment : Fragment(), View.OnClickListener {
    private lateinit var languageSpinner: Spinner
    private lateinit var dateFormatSpinner: Spinner
    private lateinit var timeFormatSpinner: Spinner

    private lateinit var binding: FragmentPreferencesBinding
    private lateinit var viewModel: PreferencesViewModel
    private val tokenLiveData = MutableLiveData<String?>()

    @SuppressLint("MissingInflatedId", "SetTextI18n", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(
                    activity!!.application,
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[PreferencesViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        languageSpinner = binding.languageSpinner
        dateFormatSpinner = binding.dateFormatSpinner
        timeFormatSpinner = binding.timeFormatSpinner

        var sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { it ->
            viewModel.onTokenUpdated(it)
        }

        val isCompany = sharedPreferences.getBoolean("isCompany", false)
        Log.d("PreferencesFragment_Company", "isCompany: $isCompany")

        val btn: Button = binding.root.findViewById(R.id.preferencesButton)
        btn.setOnClickListener(this)

        viewModel.preferencesUpdatedLiveData.observe(viewLifecycleOwner) {
            val sharedPreferences =
                requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val currentLanguage = sharedPreferences.getString("language", "")
            val currentDateFormat = sharedPreferences.getString("dateFormat", "")
            val currentTimeFormat = sharedPreferences.getString("timeFormat", "")

            val selectedLanguage =
                viewModel.languageSpinnerItems[viewModel.selectedLanguagePosition.value ?: 0]
            val selectedDateFormat =
                viewModel.dateFormatSpinnerItems[viewModel.selectedDateFormatPosition.value ?: 0]
            val selectedTimeFormat =
                viewModel.timeFormatSpinnerItems[viewModel.selectedTimeFormatPosition.value ?: 0]


            var auxLanguage = "";
            when (selectedLanguage) {
                UserLanguageApp.ES.formatString -> auxLanguage = "es"
                UserLanguageApp.EN.formatString -> auxLanguage = "en"
            }

            if (
                auxLanguage != currentLanguage
                || selectedDateFormat != currentDateFormat
                || selectedTimeFormat != currentTimeFormat
            ) {
                val editor = sharedPreferences.edit()
                editor.putString("language", auxLanguage)
                editor.putString("dateFormat", selectedDateFormat)
                editor.putString("timeFormat", selectedTimeFormat)
                editor.apply()

                val activity = requireActivity()
                activity.finish()
                activity.startActivity(activity.intent)
                activity.recreate()


            }
        }

        val languageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.languageSpinnerItems
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter

        val dateFormatAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.dateFormatSpinnerItems
        )
        dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateFormatSpinner.adapter = dateFormatAdapter

        val timeFormatAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.timeFormatSpinnerItems
        )
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFormatSpinner.adapter = timeFormatAdapter

        var languageView = binding.languageView
        var dateFormatView = binding.dateView
        var timeFormatView = binding.timeView

        val currentLanguage = sharedPreferences.getString("language", "")
        languageView.text = "$currentLanguage"

        var currentDate = Date()
        var dateFormat = sharedPreferences.getString("dateFormat", "")!!
        var xx = dateFormatted(currentDate, dateFormat)
        dateFormatView.text = "$xx"

        var currentTime = Time()
        var timeFormat = sharedPreferences.getString("timeFormat", "")!!
        currentTime.setToNow()
        var yy = timeFormatted(currentTime, timeFormat)
        timeFormatView.text = "$yy"

        return binding.root
    }





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(
                    activity!!.application,
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[PreferencesViewModel::class.java]
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.preferencesButton -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.save()
                }
            }
        }
    }

}