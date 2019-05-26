package com.barmej.weatherforecasts.utils;

import android.content.Context;
import android.util.Log;

import com.barmej.weatherforecasts.R;

/**
 * Contains useful utilities for a weather app, such as formatting wind speed to show correct unit
 * kph or mph, and from degrees to NSEW. It also contains the mapping of weather condition icon
 * codes to icon resource id
 */
public final class WeatherUtils {

    private static final String LOG_TAG = WeatherUtils.class.getSimpleName();

    /**
     * This method uses the wind direction in degrees to determine compass direction as a
     * String. (eg NW) The method will return the wind String in the following form: "2 km/h SW"
     *
     * @param context   Context to access preferences and resources
     * @param windSpeed Wind speed per hour
     * @param degrees   Degrees as measured on a compass, NOT temperature degrees!
     *                  See https://www.mathsisfun.com/geometry/degrees.html
     * @return Wind String in the following form: "2 km/h SW"
     */
    public static String getFormattedWind(Context context, double windSpeed, double degrees) {

        int windStringFormat = R.string.format_wind_kmh;

        // If the imperial system selected, use the mph unit
        String preferredMeasurementSystem = SharedPreferencesHelper.getPreferredMeasurementSystem(context);
        if (preferredMeasurementSystem.equals(context.getString(R.string.pref_units_imperial))) {
            windStringFormat = R.string.format_wind_mph;
        }

        String direction = context.getString(R.string.unknown_direction);
        if (degrees == Integer.MAX_VALUE) {
            // Leave the unknown value
            direction = context.getString(R.string.unknown_direction);
        } else if (degrees >= 337.5 || degrees < 22.5) {
            direction = context.getString(R.string.north);
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = context.getString(R.string.north_east);
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = context.getString(R.string.east);
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = context.getString(R.string.south_east);
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = context.getString(R.string.south);
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = context.getString(R.string.south_west);
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = context.getString(R.string.west);
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = context.getString(R.string.north_west);
        }
        return String.format(context.getString(windStringFormat), windSpeed, direction);
    }

    /**
     * Helper method to provide the resource ID according to the weather condition icon code
     * returned from OpenWeatherMap
     *
     * @param weatherIconId icon code from OpenWeatherMap API response
     *                      See http://openweathermap.org/weather-conditions for a list of all codes
     * @return resource ID for the corresponding icon code. -1 if no relation is found.
     */
    public static int getWeatherIcon(String weatherIconId) {

        /*
         * Based on weather code data for Open Weather Map.
         */
        switch (weatherIconId) {
            case "01d":
                return R.drawable.ic_clear_sky;
            case "01n":
                return R.drawable.ic_clear_sky_night;
            case "02d":
                return R.drawable.ic_few_clouds;
            case "02n":
                return R.drawable.ic_few_clouds_night;
            case "03d":
                return R.drawable.ic_scattered_clouds;
            case "03n":
                return R.drawable.ic_scattered_clouds_night;
            case "04d":
                return R.drawable.ic_broken_clouds;
            case "04n":
                return R.drawable.ic_broken_clouds_night;
            case "09d":
                return R.drawable.ic_shower_rain;
            case "09n":
                return R.drawable.ic_shower_rain_night;
            case "10d":
                return R.drawable.ic_shower_rain;
            case "10n":
                return R.drawable.ic_shower_rain_night;
            case "11d":
                return R.drawable.ic_thunderstrom;
            case "11n":
                return R.drawable.ic_thunderstrom_night;
            case "13d":
                return R.drawable.ic_snow;
            case "13n":
                return R.drawable.ic_snow_night;
            case "50d":
                return R.drawable.ic_mist;
            case "50n":
                return R.drawable.ic_mist_night;
        }

        Log.e(LOG_TAG, "Unknown Weather Icon: " + weatherIconId);
        return R.drawable.ic_broken_clouds;

    }


}