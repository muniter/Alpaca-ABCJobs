package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)

            when (navHostFragment?.childFragmentManager?.fragments?.firstOrNull()) {
                is AcademicInfoFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.academicInfoCreateFragment)
                    supportActionBar?.title = getString(R.string.create_academic_info_title)
                }
                is TechnicalInfoFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.technicalInfoCreateFragment)
                    supportActionBar?.title = getString(R.string.create_technical_info_title)
                }
                is WorkInfoFragment -> {
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.workInfoCreateFragment)
                    supportActionBar?.title = getString(R.string.create_work_info_title)
                }
                else -> {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        var selectedLanguage = Locale.getDefault().language;
        val selectedDateFormat = sharedPreferences.getString("dateFormat", "dd/MM/YYYY")
        val selectedTimeFormat = sharedPreferences.getString("timeFormat", "24 horas")

        if (selectedLanguage != null
            && selectedDateFormat != null
            && selectedTimeFormat != null
        ) {
            updatePreferences(selectedLanguage, selectedDateFormat, selectedTimeFormat)
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        val isCompany = sharedPreferences.getBoolean("isCompany", false)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_personal_info,
                R.id.nav_academic_info,
                R.id.nav_technical_info,
                R.id.nav_work_info,
                R.id.nav_exam_list,
                R.id.nav_interview_list,
                R.id.nav_team_list,
                R.id.nav_vacancy,
                R.id.nav_employee_list
            ), drawerLayout
        )
        if (isCompany) {
            navView.menu.findItem(R.id.nav_personal_info).isVisible = false
            navView.menu.findItem(R.id.nav_exam_list).isVisible = false
            navView.menu.findItem(R.id.nav_academic_info).isVisible = false
            navView.menu.findItem(R.id.nav_technical_info).isVisible = false
            navView.menu.findItem(R.id.nav_work_info).isVisible = false
            navView.menu.findItem(R.id.nav_interview_list).isVisible = false
        }
        else {
            navView.menu.findItem(R.id.nav_team_list).isVisible = false
            navView.menu.findItem(R.id.nav_vacancy).isVisible = false
            navView.menu.findItem(R.id.nav_employee_list).isVisible = false
        }
        navView.menu.findItem(R.id.nav_home).isVisible = true
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun hideButton() {
        binding.appBarMain.fab.hide()
    }

    fun unhideButton() {
        binding.appBarMain.fab.show()
    }

    fun updatePreferences(
        selectedLanguage: String,
        selectedDateFormat: String,
        selectedTimeFormat: String
    ) {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("timeFormat", selectedTimeFormat)
        editor.putString("dateFormat", selectedDateFormat)

        val currentLanguage = sharedPreferences.getString("language", "en")
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
        editor.apply()

        if (selectedLanguage == "Inglés" || selectedLanguage == "Español") {
            editor.apply()
            recreate()
        }
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
        val language = sharedPreferences.getString("language", "es")
        val locale = Locale(language)
        val configuration = Configuration(newBase.resources.configuration)
        configuration.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(configuration))
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.preferencesFragment)
                return true
            }
            else -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
            }
        }
    }
}