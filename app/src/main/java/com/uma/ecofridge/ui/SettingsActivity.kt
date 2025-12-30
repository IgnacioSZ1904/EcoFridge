package com.uma.ecofridge.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.uma.ecofridge.databinding.ActivitySettingsBinding
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    // Lista de opciones para el desplegable
    private val languages = listOf("Default (Sistema)", "Español", "English")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupLanguageSpinner()
    }

    private fun setupBackButton() {
        // Al pulsar la flecha, simplemente cerramos esta actividad y volvemos a la anterior
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupLanguageSpinner() {
        // 1. Crear el adaptador para el Spinner (el diseño de la lista)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        // 2. Averiguar qué idioma tenemos seleccionado actualmente para marcarlo en la lista
        val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val selectedPosition = when {
            currentLocale.contains("es") -> 1 // Español está en la posición 1 de la lista
            currentLocale.contains("en") -> 2 // Inglés está en la posición 2
            else -> 0 // Default
        }
        binding.spinnerLanguage.setSelection(selectedPosition)

        // 3. Detectar cuando el usuario cambia la opción
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Solo cambiamos si la selección es diferente a la actual para evitar bucles
                if (position != selectedPosition) {
                    changeLanguage(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun changeLanguage(position: Int) {
        val localeCode = when (position) {
            1 -> "es" // Español
            2 -> "en" // English
            else -> null // Default (null borrará la preferencia y usará la del sistema)
        }

        // Esta es la forma MODERNA (Android 13+) y compatible hacia atrás de cambiar el idioma
        val appLocale: LocaleListCompat = if (localeCode != null) {
            LocaleListCompat.forLanguageTags(localeCode)
        } else {
            LocaleListCompat.getEmptyLocaleList() // Vuelve a Default
        }

        AppCompatDelegate.setApplicationLocales(appLocale)

        // Android recreará la actividad automáticamente para aplicar el idioma,
        // pero a veces queda mejor reiniciar para asegurar que todo se traduzca.
    }
}