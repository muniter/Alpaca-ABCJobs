package com.example.abc_jobs_alpaca.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.Time
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abc_jobs_alpaca.R
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.abc_jobs_alpaca.databinding.FragmentPreferencesBinding
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.viewmodel.PreferencesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PreferencesFragment : Fragment() {
    private lateinit var languageSpinner: Spinner
    private lateinit var dateFormatSpinner: Spinner
    private lateinit var timeFormatSpinner: Spinner

    private lateinit var binding: FragmentPreferencesBinding
    private lateinit var viewModel: PreferencesViewModel
    private val tokenLiveData = MutableLiveData<String?>()

    companion object {
        fun newInstance() = PreferencesFragment()
        const val DATE_FORMAT_1 = "dd/MM/yyyy"
        const val DATE_FORMAT_2 = "dd-MM-yyyy"
        const val DATE_FORMAT_3 = "yyyy/MM/dd"
        const val DATE_FORMAT_4 = "yyyy-MM-dd"
        const val TIME_FORMAT_1 = "12h"
        const val TIME_FORMAT_2 = "24h"

    }

    @SuppressLint("MissingInflatedId", "SetTextI18n", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(activity!!.application) as T
            }
        })[PreferencesViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        languageSpinner = binding.languageSpinner
        dateFormatSpinner = binding.dateFormatSpinner
        timeFormatSpinner = binding.timeFormatSpinner

        var sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { it ->
            viewModel.onTokenUpdated(it)
        }

        viewModel.preferencesUpdatedLiveData.observe(viewLifecycleOwner) {
            val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val currentLanguage = sharedPreferences.getString("language", "")
            val currentDateFormat = sharedPreferences.getString("dateFormat", "")
            val currentTimeFormat = sharedPreferences.getString("timeFormat", "")

            val selectedLanguage = viewModel.languageSpinnerItems[viewModel.selectedLanguagePosition.value ?: 0]
            val selectedDateFormat = viewModel.dateFormatSpinnerItems[viewModel.selectedDateFormatPosition.value ?: 0]
            val selectedTimeFormat = viewModel.timeFormatSpinnerItems[viewModel.selectedTimeFormatPosition.value ?: 0]


            var auxLanguage ="";
            when(selectedLanguage){
                UserLanguageApp.ES.formatString-> auxLanguage = "es"
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

        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.languageSpinnerItems)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter

        val dateFormatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.dateFormatSpinnerItems)
        dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateFormatSpinner.adapter = dateFormatAdapter

        val timeFormatAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.timeFormatSpinnerItems)
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFormatSpinner.adapter = timeFormatAdapter

        var languageView = binding.languageView
        var dateFormatView = binding.dateView
        var timeFormatView = binding.timeView

        val currentLanguage = sharedPreferences.getString("language", "")
        languageView.text = "$currentLanguage"

        var currentDate = Date()
        var xx = dateFormatted(currentDate)
        dateFormatView.text = "$xx"

        var currentTime = Time()
        currentTime.setToNow()
        var yy = timeFormatted(currentTime)
        timeFormatView.text = "$yy"


        return binding.root
    }



    private fun dateFormatted(date : Date): String {
        val dateString: String;
        var sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var dateFormat = sharedPreferences.getString("dateFormat", "")
        when(dateFormat){
            DATE_FORMAT_1 -> dateString = SimpleDateFormat(DATE_FORMAT_1, Locale.getDefault()).format(date)
            DATE_FORMAT_2 -> dateString = SimpleDateFormat(DATE_FORMAT_2, Locale.getDefault()).format(date)
            DATE_FORMAT_3-> dateString = SimpleDateFormat(DATE_FORMAT_3, Locale.getDefault()).format(date)
            DATE_FORMAT_4 -> dateString = SimpleDateFormat(DATE_FORMAT_4, Locale.getDefault()).format(date)
            else -> dateString = ""
        }
        return dateString
    }

    private fun timeFormatted(time: Time): String {
        val timeString: String
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val timeFormat = sharedPreferences.getString("timeFormat", TIME_FORMAT_1) // Obtiene la preferencia de formato de hora

        val formatPattern = if (timeFormat == TIME_FORMAT_2) {
            "HH:mm" // Formato de 24 horas
        } else {
            "hh:mm a" // Formato de 12 horas con AM/PM
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)

        timeString = SimpleDateFormat(formatPattern, Locale.getDefault()).format(calendar.time)

        return timeString
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PreferencesViewModel(activity!!.application) as T
            }
        })[PreferencesViewModel::class.java]
    }

}