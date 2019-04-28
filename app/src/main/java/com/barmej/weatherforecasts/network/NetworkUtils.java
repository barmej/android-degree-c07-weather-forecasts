package com.barmej.weatherforecasts.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.barmej.weatherforecasts.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;


/**
 * This utility class will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*
     * OpenWeatherMap's API Url
     */
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

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
     * Object used for the purpose of synchronize lock
     */
    private static final Object LOCK = new Object();


    /**
     * Instance of this class for Singleton
     */
    private static NetworkUtils sInstance;

    /**
     * Instance of the application context
     */
    private Context mContext;

    /**
     * Instance of Volley Request Queue
     */
    private RequestQueue mRequestQueue;


    /**
     * @param context Context to use for some initializations
     */
    private NetworkUtils(Context context) {
        // getApplicationContext() is key, it keeps your application safe from leaking the
        // Activity or BroadcastReceiver if you pass it instead of application context
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    /**
     * Method used to get an instance of NetworkUtils class
     *
     * @param context Context to use for some initializations
     * @return an instance of NetworkUtils class
     */
    public static synchronized NetworkUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) sInstance = new NetworkUtils(context);
            }
        }
        return sInstance;
    }

    /**
     * Get an instance of Volley RequestQueue
     *
     * @return an instance of Volley RequestQueue
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    /**
     * @param request volley request to add to RequestQueue
     * @param <T>     The passed-in request
     */
    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

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

}