package com.example.abc_jobs_alpaca

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.abc_jobs_alpaca.viewmodel.PreferencesViewModel
import androidx.preference.PreferenceFragmentCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PreferencesFragment : Fragment() {
    private lateinit var languageSpinner: Spinner
    private lateinit var dateFormatSpinner: Spinner
    private lateinit var timeFormatSpinner: Spinner
    companion object {
        fun newInstance() = PreferencesFragment()
    }

    private lateinit var viewModel: PreferencesViewModel
    private lateinit var savePreferencesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_preferences, container, false)
        languageSpinner = view.findViewById(R.id.languageSpinner)
        dateFormatSpinner = view.findViewById(R.id.dateFormatSpinner)
        timeFormatSpinner = view.findViewById(R.id.timeFormatSpinner)

        val languageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_options,
            android.R.layout.simple_spinner_item
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter

        val dateFormatAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.date_format_options,
            android.R.layout.simple_spinner_item
        )
        dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateFormatSpinner.adapter = dateFormatAdapter

        val timeFormatAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.time_format_options,
            android.R.layout.simple_spinner_item
        )
        timeFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeFormatSpinner.adapter = timeFormatAdapter

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        val selectedLanguage = Locale.getDefault().language
        val selectedDateFormat = sharedPreferences.getString("dateFormat", "DD/MM/YYYY")
        val selectedTimeFormat = sharedPreferences.getString("timeFormat", "24 horas")
        val languagePosition = languageAdapter.getPosition(selectedLanguage)
        val dateFormatPosition = dateFormatAdapter.getPosition(selectedDateFormat)
        val timeFormatPosition = timeFormatAdapter.getPosition(selectedTimeFormat)

        languageSpinner.setSelection(languagePosition)
        dateFormatSpinner.setSelection(dateFormatPosition)
        timeFormatSpinner.setSelection(timeFormatPosition)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languageSpinner.selectedItem.toString()
                saveLanguagePreferences(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        dateFormatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDateFormat = dateFormatSpinner.selectedItem.toString()
                saveDateFormatPreference(selectedDateFormat)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        timeFormatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTimeFormat = timeFormatSpinner.selectedItem.toString()
                saveTimeFormatPreference(selectedTimeFormat)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        savePreferencesButton = view?.findViewById(R.id.preferencesButton)!!
        savePreferencesButton.setOnClickListener {
            savePreferences()
        }

        // Obtén una referencia al TextView donde mostrar la hora
        val textViewHora = view.findViewById<TextView>(R.id.textViewHora)
        val timeFormatPreference = sharedPreferences.getString("timeFormat", "24 horas")

        val horaActual = timeFormatPreference?.let { obtenerHoraActual(it) }
        textViewHora.text = "Hora actual: $horaActual"
        return view
    }

    private fun savePreferences() {
        val selectedLanguage = languageSpinner.selectedItem.toString()
        val selectedDateFormat = dateFormatSpinner.selectedItem.toString()
        val selectedTimeFormat = timeFormatSpinner.selectedItem.toString()

        if (activity is MainActivity) {
            (activity as MainActivity).updatePreferences(selectedLanguage, selectedDateFormat, selectedTimeFormat)
        }
    }

    private fun saveLanguagePreferences(selectedLanguage: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", selectedLanguage)
        editor.apply()
    }

    private fun saveDateFormatPreference(selectedDateFormat: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("dateFormat", selectedDateFormat)
        editor.apply()
    }

    private fun saveTimeFormatPreference(selectedTimeFormat: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("timeFormat", selectedTimeFormat)
        editor.apply()
    }

    private fun obtenerHoraActual(timeFormat: String): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val formattedHour: String
        if (timeFormat == "12 horas") {
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            formattedHour = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
        } else {
            formattedHour = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        }

        return formattedHour
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PreferencesViewModel::class.java)
    }
}
