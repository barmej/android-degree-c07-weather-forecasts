package com.barmej.weatherforecasts.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.barmej.weatherforecasts.R;

/**
 * A helper class used to access SharedPreferences file and read user preferences
 */
public final class SharedPreferencesHelper {

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "State of Kuwait", the home of the Barmej.com!
     *
     * @param context Context used to access SharedPreferences
     * @return Location the location set in SharedPreferences.
     */
    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String locationKey = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return sp.getString(locationKey, defaultLocation);
    }

    /**
     * Returns the preferred measurement system set in Preferences. The default is metric
     *
     * @param context Context used to get the SharedPreferences
     * @return metric or imperial
     */
    public static String getPreferredMeasurementSystem(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String unitKey = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        return sp.getString(unitKey, defaultUnits);
    }


}