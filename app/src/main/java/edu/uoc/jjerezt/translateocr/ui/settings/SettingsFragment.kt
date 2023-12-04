package edu.uoc.jjerezt.translateocr.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import edu.uoc.jjerezt.translateocr.MainActivity
import edu.uoc.jjerezt.translateocr.R
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

class SettingsFragment : PreferenceFragmentCompat() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private suspend fun markUnknown() {
        println("We're going to mark unknown words")
        val mark = booleanPreferencesKey("mark")
        context?.dataStore?.edit { settings ->
            if(settings[mark] == false){
                settings[mark] = true
            }
        }
    }

    private suspend fun markAmbiguity() {
        println("We're going to mark ambiguity")
        val ambiguity = booleanPreferencesKey("ambiguity")
        context?.dataStore?.edit { settings ->
            if(settings[ambiguity] == false){
                settings[ambiguity] = true
            }
        }
    }

    private suspend fun unmarkAmbiguity() {
        println("We're going to unmark ambiguity")
        val ambiguity = booleanPreferencesKey("ambiguity")
        context?.dataStore?.edit { settings ->
            if(settings[ambiguity] == true){
                settings[ambiguity] = false
            }
        }
    }

    private suspend fun unmarkUnknown() {
        println("We're going to unmark unknown words")
        val mark = booleanPreferencesKey("mark")
        context?.dataStore?.edit { settings ->
            if(settings[mark] == true){
                settings[mark] = false
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        // https://developer.android.com/guide/topics/ui/settings/customize-your-settings
        val unknownWords: Preference? = findPreference("words") as Preference?
        unknownWords?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference ->
                val sharedPreferences = preference.sharedPreferences
                val unknownWordsValue = sharedPreferences?.getBoolean("words", false)
                if (unknownWordsValue == true){
                    runBlocking {
                        withTimeoutOrNull(2000) { // UI can freeze for 2 seconds
                            markUnknown()
                        }
                    }
                }
                else{
                    runBlocking {
                        withTimeoutOrNull(2000) { // UI can freeze for 2 seconds
                            unmarkUnknown()
                        }
                    }
                }
                true
            }

        val ambiguityWords : Preference? = findPreference("ambiguity") as Preference?
        ambiguityWords!!.onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference ->
                val sharedPreferences = preference.sharedPreferences
                val unknownWordsValue = sharedPreferences?.getBoolean("ambiguity", false)
                if (unknownWordsValue == true){
                    runBlocking {
                        withTimeoutOrNull(2000) { // UI can freeze for 2 seconds
                            markAmbiguity()
                        }
                    }
                }
                else{
                    runBlocking {
                        withTimeoutOrNull(2000) { // UI can freeze for 2 seconds
                            unmarkAmbiguity()
                        }
                    }
                }
                true;
            }

        val license : Preference? = findPreference("license") as Preference?
        license!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val mySnackbar = view?.let { it1 -> Snackbar.make(it1, "General Public License 3 (GPL 3)", 5000) }
            mySnackbar?.show()
            true
        }

        val about : Preference? = findPreference("about") as Preference?
        about!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val mySnackbar = view?.let { it1 -> Snackbar.make(it1,
                "TranslateOCR\n" +
                    "(c) 2023-2024 Joan Jerez\n" +
                        "Master's Degree Final Project", 5000).setTextMaxLines(3) }
            mySnackbar?.show()
            true
        }

        // https://stackoverflow.com/questions/21028786/how-do-i-open-a-new-fragment-from-another-fragment
        /* val dictionaries: Preference? = findPreference("dictionaries") as Preference?
        dictionaries!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                view?.let { Navigation.findNavController(it).navigate(R.id.action_go) };
            }
            catch (e: Exception){
                println(e.localizedMessage)
            }
            true
        } */
    }
}