package com.barmej.weatherforecasts.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.barmej.weatherforecasts.R;


/**
 * The SettingsFragment serves as the display for all of the user's settings. From this fragment the
 * users will be able to change their preference for units of measurement from metric to imperial
 * and set their preferred weather location
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Show preferences defined in the XML file
        addPreferencesFromResource(R.xml.settings);

        /*
         * Loop through all preference and update their summary to show user preferences
         * saved in the SharedPreferences file
         */
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            setPreferenceSummary(preference);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Update preference summary
        Preference preference = findPreference(key);
        if (preference != null) {
            setPreferenceSummary(preference);
        }

        // Set result to notify the MainActivity of the change
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK);
        }

    }

    private void setPreferenceSummary(Preference preference) {

        // Get the value associated with the preference key from the SharedPreferences file
        String value = preference.getSharedPreferences().getString(preference.getKey(), "");

        // If the value is empty that means it's not set yet so we should not update the summary
        if (TextUtils.isEmpty(value)) {
            return;
        }

        if (preference instanceof ListPreference) {
            /*
             * For list preferences, look up the correct display value in
             * the preference's 'entries' list (since they have separate labels/values).
             */
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the value's string as a summary.
            preference.setSummary(value);
        }


    }


}
