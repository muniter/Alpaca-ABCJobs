package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

class MainActivity : AppCompatActivity()
{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        var selectedLanguage = Locale.getDefault().language;
        val selectedDateFormat = sharedPreferences.getString("dateFormat", "DD/MM/YYYY")
        val selectedTimeFormat = sharedPreferences.getString("timeFormat", "24 horas")

        if (selectedLanguage != null
            && selectedDateFormat != null
            && selectedTimeFormat != null)
            {
                    updatePreferences(selectedLanguage, selectedDateFormat, selectedTimeFormat)
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun updatePreferences(selectedLanguage: String, selectedDateFormat: String, selectedTimeFormat: String) {
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
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.preferencesFragment)
                true
            }
            R.id.nav_gallery -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate((R.id.nav_gallery))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}