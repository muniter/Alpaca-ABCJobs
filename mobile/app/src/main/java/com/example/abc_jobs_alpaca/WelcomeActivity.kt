package com.example.abc_jobs_alpaca


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.navigation.Navigation.findNavController
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterModel
import java.util.Locale

class WelcomeActivity: AppCompatActivity()
    , WelcomeFragment.OnLanguageChangeListener
    , WelcomeFragment.OnElementHideListener{
    private lateinit var viewModel: CandidateRegisterModel
    private val toastMessage = MutableLiveData<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        viewModel = ViewModelProvider(this).get(CandidateRegisterModel::class.java)

        viewModel.getToastMessage().observe(this, Observer { message ->
            showToast(message)
        })

        val spinner: Spinner = findViewById(R.id.spinner)
        val languageOptions = resources.getStringArray(R.array.language_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view != null) {
                    val selectedLanguage = languageOptions[position]
                    var currentLanguage = Configuration(resources.configuration).locales.get(0).toString();
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
            }
        }

    }
    override fun hideElement(elementId: Int) {
        val elementToHide = findViewById<View>(elementId)
        elementToHide.visibility = View.GONE
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
        val sharedPreferences = newBase.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "en") // "en" es el idioma predeterminado
        val locale = Locale(language)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
