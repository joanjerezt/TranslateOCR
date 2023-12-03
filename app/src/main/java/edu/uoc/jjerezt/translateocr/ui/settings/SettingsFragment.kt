package edu.uoc.jjerezt.translateocr.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import edu.uoc.jjerezt.translateocr.R

class SettingsFragment : PreferenceFragmentCompat() {


    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val unknownWords: Preference? = findPreference("words") as Preference?
        unknownWords!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            fun onPreferenceClick(preference: Preference?): Boolean {
                // open browser or intent here
                return true
            }
            override fun onPreferenceClick(preference: Preference): Boolean {
                if (preference.equals(true)){
                    TODO()

                   // Context.dataStore.edit { settings ->
                   //     val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
                   //     settings[EXAMPLE_COUNTER] = currentCounterValue + 1
                   // }

                }
                return true
            }
        }

        val ambiguityWords : Preference? = findPreference("ambiguity") as Preference?
        ambiguityWords!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            fun onPreferenceClick(preference: Preference?): Boolean {
                // open browser or intent here
                return true
            }
            override fun onPreferenceClick(preference: Preference): Boolean {
                if (preference.equals(true)){
                    println(preference)

                    // Context.dataStore.edit { settings ->
                    //     val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
                    //     settings[EXAMPLE_COUNTER] = currentCounterValue + 1
                    // }

                }
                return true;
            }
        }

        val license : Preference? = findPreference("license") as Preference?
        license!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            fun onPreferenceClick(preference: Preference?): Boolean {
                // open browser or intent here
                return true
            }
            override fun onPreferenceClick(preference: Preference): Boolean {
                TODO("Open License popup")
            }
        }

        val about : Preference? = findPreference("about") as Preference?
        about!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            fun onPreferenceClick(preference: Preference?): Boolean {
                // open browser or intent here
                return true
            }
            override fun onPreferenceClick(preference: Preference): Boolean {
                TODO("Open About popup")
            }
        }


    }
}