package com.barmej.weatherforecasts.utils;

import android.util.Log;

import com.barmej.weatherforecasts.entity.Forecast;
import com.barmej.weatherforecasts.entity.ForecastLists;
import com.barmej.weatherforecasts.entity.Main;
import com.barmej.weatherforecasts.entity.Sys;
import com.barmej.weatherforecasts.entity.Weather;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.entity.Wind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
     * Sunrise and Sunset times
     */
    private static final String OWM_SYS = "sys";
    private static final String OWM_SUNRISE = "sunrise";
    private static final String OWM_SUNSET = "sunset";



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
     * @param weatherJson response json we got from OpenWeatherMap weather endpoint
     * @return WeatherInfo object that carries all the weather information extracted from JSON string
     * @throws JSONException exception that occurs if there is an error happened while parsing JSON
     */
    public static WeatherInfo getWeatherInfoObjectFromJson(JSONObject weatherJson) throws JSONException {

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

        // Sunrise and sunset times
        JSONObject sysObject = weatherJson.getJSONObject(OWM_SYS);

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
        wind.setDeg(windObject.has(OWM_WIND_DIRECTION) ? windObject.getLong(OWM_WIND_DIRECTION) : Integer.MAX_VALUE);
        weatherInfo.setWind(wind);
        Weather weather = new Weather();
        weather.setDescription(weatherObject.getString(OWM_WEATHER_DESCRIPTION));
        weather.setIcon(weatherObject.getString(OWM_WEATHER_ICON));
        List<Weather> weatherList = new ArrayList<>();
        weatherList.add(weather);
        weatherInfo.setWeather(weatherList);
        weatherInfo.setName(weatherJson.has(OWM_CITY_NAME) ? weatherJson.getString(OWM_CITY_NAME) : "");
        Sys sys = new Sys();
        sys.setSunrise(sysObject.getLong(OWM_SUNRISE));
        sys.setSunset(sysObject.getLong(OWM_SUNSET));
        weatherInfo.setSys(sys);
        return weatherInfo;

    }

    /**
     * This method parses JSON from a web response and returns a java object contain the forecasts
     * data over various days.
     *
     * @param forecastsJson response json we got from OpenWeatherMap forecast endpoint
     * @return Object of {@link ForecastLists} contains two arrays, the first one for the next 24hrs forecast and the seconds
     * for the next 4 days forecasts
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ForecastLists getForecastsDataFromJson(JSONObject forecastsJson) throws JSONException {

        if (isError(forecastsJson)) {
            return null;
        }

        JSONArray jsonForecastsArray = forecastsJson.getJSONArray(OWM_LIST);

        List<Forecast> hoursForecasts = new ArrayList<>();
        LinkedHashMap<String, List<Forecast>> daysForecasts = new LinkedHashMap<>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentDay = df.format(new Date());
        int hoursForecastsCount = 0;

        for (int i = 0; i < jsonForecastsArray.length(); i++) {

            JSONObject singleForecastJson = jsonForecastsArray.getJSONObject(i);

            // Weather description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = singleForecastJson.getJSONArray(OWM_WEATHER).getJSONObject(0);

            // Temperatures are sent by OpenWeatherMap in a child object called Main
            JSONObject mainObject = singleForecastJson.getJSONObject(OWM_MAIN);

            // Wind speed and direction are wrapped in a Wind object
            JSONObject windObject = singleForecastJson.getJSONObject(OWM_WIND);

            Forecast forecast = new Forecast();
            forecast.setDt(singleForecastJson.getLong(OWM_DATE));
            forecast.setDtTxt(singleForecastJson.getString(OWM_DATE_TEXT));
            Main main = new Main();
            main.setTemp(mainObject.getDouble(OWM_TEMPERATURE));
            main.setTempMax(mainObject.getDouble(OWM_MAX));
            main.setTempMin(mainObject.getDouble(OWM_MIN));
            main.setHumidity(mainObject.getInt(OWM_HUMIDITY));
            main.setPressure(mainObject.getLong(OWM_PRESSURE));
            forecast.setMain(main);
            Wind wind = new Wind();
            wind.setSpeed(windObject.getDouble(OWM_WINDSPEED));
            wind.setDeg(windObject.getLong(OWM_WIND_DIRECTION));
            forecast.setWind(wind);
            Weather weather = new Weather();
            weather.setDescription(weatherObject.getString(OWM_WEATHER_DESCRIPTION));
            weather.setIcon(weatherObject.getString(OWM_WEATHER_ICON));
            List<Weather> weatherList = new ArrayList<>();
            weatherList.add(weather);
            forecast.setWeather(weatherList);

            if (hoursForecastsCount++ < 8) {
                hoursForecasts.add(forecast);
            }

            String date = forecast.getDtTxt().split(" ")[0];

            if (!date.equals(currentDay)) {
                if (daysForecasts.containsKey(date)) {
                    List<Forecast> forecasts = daysForecasts.get(date);
                    assert forecasts != null;
                    forecasts.add(forecast);
                } else {
                    List<Forecast> forecasts = new ArrayList<>();
                    forecasts.add(forecast);
                    daysForecasts.put(date, forecasts);
                }
            }

        }

        ForecastLists forecastsData = new ForecastLists();
        forecastsData.setHoursForecasts(hoursForecasts);
        List<List<Forecast>> listOfDaysForecasts = new ArrayList<>();
        for (Map.Entry entry : daysForecasts.entrySet()) {
            listOfDaysForecasts.add((List<Forecast>) entry.getValue());
        }
        forecastsData.setDaysForecasts(listOfDaysForecasts);

        return forecastsData;
    }


}
