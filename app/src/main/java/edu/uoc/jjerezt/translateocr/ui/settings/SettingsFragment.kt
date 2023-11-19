package edu.uoc.jjerezt.translateocr.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import edu.uoc.jjerezt.translateocr.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}