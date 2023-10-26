package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterViewModel
import java.util.Locale

class WelcomeActivity: AppCompatActivity()
    , WelcomeFragment.OnLanguageChangeListener
    , WelcomeFragment.OnElementHideListener {
    private lateinit var viewModel: CandidateRegisterViewModel
    private var elementHideListener: WelcomeFragment.OnElementHideListener? = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        elementHideListener = this
        setContentView(R.layout.activity_welcome)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CandidateRegisterViewModel(ABCJobsRepository(application), MutableLiveData<MessageEvent>()) as T
            }
        })[CandidateRegisterViewModel::class.java]

        val spinner: Spinner = findViewById(R.id.spinner)
        val languageOptions = resources.getStringArray(R.array.language_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    val selectedLanguage = languageOptions[position]
                    var currentLanguage = Configuration(resources.configuration).locales[0].toString();

                    when (selectedLanguage) {
                        "Inglés" -> {
                            if (currentLanguage != "en") {
                                setLocale("en")
                                recreate()
                            }
                        }
                        "Español" -> {
                            if (currentLanguage != "es") {
                                setLocale("es")
                                recreate()
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                setLocale("en")
                recreate()
            }
        }
    }

    override fun onLanguageSelected(newLanguage: String) {
        setLocale(newLanguage)
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en")
        val editor = sharedPreferences.edit()
        editor.putString("dateFormat", "DD/MM/YYYY")
        editor.putString("timeFormat", "24 horas")
        editor.putString("language", language)
        editor.apply()
        val locale = Locale(language)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    override fun hideElement(elementId: Int) {
        val elementToHide = findViewById<View>(elementId)
        elementToHide.visibility = View.INVISIBLE
    }
}