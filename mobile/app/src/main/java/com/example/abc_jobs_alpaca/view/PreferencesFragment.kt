package com.example.abc_jobs_alpaca.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import java.util.Calendar
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
    }

    @SuppressLint("MissingInflatedId", "SetTextI18n", "SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferences, container, false)
        viewModel = ViewModelProvider(this)[PreferencesViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        languageSpinner = binding.languageSpinner
        dateFormatSpinner = binding.dateFormatSpinner
        timeFormatSpinner = binding.timeFormatSpinner

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { it ->
            viewModel.onTokenUpdated(it)
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



            return binding.root
        }

        private fun getCurrentHour(timeFormat: String): String {
            val calendar = Calendar.getInstance()
            val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
            val minute = calendar[Calendar.MINUTE]

            val formattedHour: String
            if (timeFormat == "12 horas") {
                val amPm = if (hourOfDay < 12) "AM" else "PM"
                val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                formattedHour =
                    String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
            } else {
                formattedHour = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            }

            return formattedHour
        }

        private fun getCurrentDate(dateFormat: String): String {
            val calendar = Calendar.getInstance()
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val day = calendar[Calendar.DAY_OF_MONTH]

            val formattedDate: String
            if (dateFormat == "DD/MM/YYYY") {
                formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year)
            } else if (dateFormat == "MM/DD/YYYY") {
                formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%04d", month, day, year)
            } else {
                formattedDate = ""
            }
            return formattedDate
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