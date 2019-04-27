package com.barmej.weatherforecasts.utils;

import android.util.Log;

import com.barmej.weatherforecasts.entity.Main;
import com.barmej.weatherforecasts.entity.Weather;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.entity.Wind;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This utility contains methods to handle OpenWeatherMap JSON data.
 */
public class OpenWeatherDataParser {

    private static final String TAG = OpenWeatherDataParser.class.getSimpleName();

    /**
     * Operation status code
     */
    private static final String OWM_MESSAGE_CODE = "cod";

    /**
     * Location information
     */
    private static final String OWM_CITY = "city";
    private static final String OWM_CITY_NAME = "name";

    /**
     * Weather information list
     * Each day's forecast info is an element of the "list" array
     */
    private static final String OWM_LIST = "list";

    /**
     * Date and time
     */
    private static final String OWM_DATE = "dt";
    private static final String OWM_DATE_TEXT = "dt_txt";

    /**
     * Wind information
     */
    private static final String OWM_WIND = "wind";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    /**
     * Main weather Information
     */
    private static final String OWM_MAIN = "main";
    private static final String OWM_TEMPERATURE = "temp";
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";

    /**
     * Weather condition information
     */
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_DESCRIPTION = "description";
    private static final String OWM_WEATHER_ICON = "icon";


    /**
     * Check if there is an error in the response json
     *
     * @param jsonObject the response json object that we got from the server
     * @return whenever true if the response code = 200, false otherwise
     */
    private static boolean isError(JSONObject jsonObject) {
        try {
            // Check the response code to see if there is an error
            if (jsonObject.has(OWM_MESSAGE_CODE)) {
                int errorCode = jsonObject.getInt(OWM_MESSAGE_CODE);
                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        return false;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        Log.e(TAG, "Location Invalid");
                    default:
                        Log.e(TAG, "Server probably down");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param weatherJsonString response json string we got from OpenWeatherMap weather endpoint
     * @return WeatherInfo object that carries all the weather information extracted from JSON string
     * @throws JSONException exception that occurs if there is an error happened while parsing JSON
     */
    public static WeatherInfo getWeatherInfoObjectFromJson(String weatherJsonString) throws JSONException {

        // Convert json string to JSONObject
        JSONObject weatherJson = new JSONObject(weatherJsonString);

        // Check if there is an error in the json
        if (isError(weatherJson)) {
            return null;
        }

        // Weather description is in a child array called "weather", which is 1 element long.
        JSONObject weatherObject = weatherJson.getJSONArray(OWM_WEATHER).getJSONObject(0);

        // Temperatures are sent by OpenWeatherMap in a child object called Main
        JSONObject mainObject = weatherJson.getJSONObject(OWM_MAIN);

        // Wind speed and direction are wrapped in a Wind object
        JSONObject windObject = weatherJson.getJSONObject(OWM_WIND);

        // Get data from Json and assign it to Java object
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setDt(weatherJson.getLong(OWM_DATE));
        Main main = new Main();
        main.setTemp(mainObject.getDouble(OWM_TEMPERATURE));
        main.setTempMax(mainObject.getDouble(OWM_MAX));
        main.setTempMin(mainObject.getDouble(OWM_MIN));
        main.setHumidity(mainObject.getInt(OWM_HUMIDITY));
        main.setPressure(mainObject.getLong(OWM_PRESSURE));
        weatherInfo.setMain(main);
        Wind wind = new Wind();
        wind.setSpeed(windObject.getDouble(OWM_WINDSPEED));
        wind.setDeg(windObject.getLong(OWM_WIND_DIRECTION));
        weatherInfo.setWind(wind);
        Weather weather = new Weather();
        weather.setDescription(weatherObject.getString(OWM_WEATHER_DESCRIPTION));
        weather.setIcon(weatherObject.getString(OWM_WEATHER_ICON));
        List<Weather> weatherList = new ArrayList<>();
        weatherList.add(weather);
        weatherInfo.setWeather(weatherList);
        weatherInfo.setName(weatherJson.has(OWM_CITY_NAME) ? weatherJson.getString(OWM_CITY_NAME) : "");
        return weatherInfo;

    }


}
