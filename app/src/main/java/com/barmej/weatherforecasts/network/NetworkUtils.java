package com.barmej.weatherforecasts.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.barmej.weatherforecasts.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;


/**
 * This utility class will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*
     * OpenWeatherMap's API Url
     */
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    /**
     * Current Weather endpoint
     */
    private static final String WEATHER_ENDPOINT = "weather";

    /**
     * Coming forecasts endpoints
     */
    private static final String FORECAST_ENDPOINT = "forecast";

    /**
     * The query parameter allows us to determine the location
     */
    private static final String QUERY_PARAM = "q";


    /* The FORMAT parameter allows us to designate whether we want JSON or XML from our API */
    private static final String FORMAT_PARAM = "mode";

    /**
     * Units parameter allows us to designate whether we want metric units or imperial units
     */
    private static final String UNITS_PARAM = "units";

    /**
     * Lang parameter to specify the language of the response
     */
    private static final String LANG_PARAM = "lang";

    /**
     * The app id allow us to pass our API key to OpenWeatherMap to be a valid response
     */
    private static final String APP_ID_PARAM = "appid";

    /**
     * The FORMAT we want our API to return
     */
    private static final String FORMAT = "json";

    /**
     * The units constant values
     */
    private static final String METRIC = "metric";
    private static final String IMPERIAL = "imperial";


    /**
     * @return the url for the weather endpoint
     */
    public static URL getWeatherUrl(Context context) {
        return buildUrl(context, WEATHER_ENDPOINT);
    }

    /**
     * @return The url for the forecasts endpoint
     */
    public static URL getForecastUrl(Context context) {
        return buildUrl(context, FORECAST_ENDPOINT);
    }

    /**
     * Builds the URL to get the weather data using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param context  context object to use for reading string resources
     * @param endPoint the end point to get data from
     * @return The URL to use to query the weather server.
     */
    private static URL buildUrl(Context context, String endPoint) {
        Uri.Builder uriBuilder = Uri.parse(BASE_URL + endPoint).buildUpon();
        Uri uri = uriBuilder
                .appendQueryParameter(QUERY_PARAM, context.getString(R.string.pref_location_default))
                .appendQueryParameter(FORMAT_PARAM, FORMAT)
                .appendQueryParameter(UNITS_PARAM, METRIC)
                .appendQueryParameter(LANG_PARAM, Locale.getDefault().getLanguage())
                .appendQueryParameter(APP_ID_PARAM, context.getString(R.string.api_key))
                .build();
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response as String
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            Log.d(TAG, "Response: " + response);
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

}