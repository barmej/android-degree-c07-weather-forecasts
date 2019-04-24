package com.barmej.weatherforecasts;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.weatherforecasts.adapters.DaysForecastAdapter;
import com.barmej.weatherforecasts.adapters.HoursForecastAdapter;
import com.barmej.weatherforecasts.entity.Forecast;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity that show current weather info, next hours & days forecasts
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mIconView;
    private TextView mCityNameTextView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mTempTextView;
    private TextView mHighLowTempView;

    private HoursForecastAdapter mHoursForecastAdapter;
    private DaysForecastAdapter mDaysForecastsAdapter;

    private RecyclerView mHoursForecastsRecyclerView;
    private RecyclerView mDaysForecastRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize member variables
        mIconView = findViewById(R.id.weather_icon);
        mCityNameTextView = findViewById(R.id.city);
        mDateView = findViewById(R.id.date);
        mDescriptionView = findViewById(R.id.weather_description);
        mTempTextView = findViewById(R.id.temperature);
        mHighLowTempView = findViewById(R.id.high_low_temperature);


        // Create new HoursForecastAdapter and set it to RecyclerView
        mHoursForecastAdapter = new HoursForecastAdapter(this);
        mHoursForecastsRecyclerView = findViewById(R.id.rv_hours_forecast);
        mHoursForecastsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHoursForecastsRecyclerView.setAdapter(mHoursForecastAdapter);

        // Create new DaysForecastAdapter and set it to RecyclerView
        mDaysForecastsAdapter = new DaysForecastAdapter(this);
        mDaysForecastRecyclerView = findViewById(R.id.rv_days_forecast);
        mDaysForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDaysForecastRecyclerView.setAdapter(mDaysForecastsAdapter);

        // Add dummy empty objects to hours forecasts list
        List<Forecast> hourForecasts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            hourForecasts.add(new Forecast());
        }

        // Add dummy empty objects to days forecasts list
        List<List<Forecast>> daysForecasts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Forecast> hoursForecasts = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                hourForecasts.add(new Forecast());
            }
            daysForecasts.add(hoursForecasts);
        }

        // Show current weather info
        showWeatherInfo();

        // Show forecasts lists
        mHoursForecastAdapter.updateData(hourForecasts);
        mDaysForecastsAdapter.updateData(daysForecasts);

    }


    private void showWeatherInfo() {

        /* Weather Icon ************************************************************************* */

        // Get the weather icon resource id based on icon string passed from the api
        int weatherImageId = R.drawable.ic_clear_sky;

        // Display weather condition icon
        mIconView.setImageResource(weatherImageId);

        /* Current city ************************************************************************* */

        // Read date from weather info object
        String cityName = "State of Kuwait";

        // Display city name
        mCityNameTextView.setText(cityName);

        /* Weather Date ************************************************************************* */

        // Get human readable string using getFriendlyDateString utility method and display it
        String dateString = "Wed, 24 April";

        /* Display friendly date string */
        mDateView.setText(dateString);

        /* Weather Description ****************************************************************** */

        // Get weather condition description
        String description = "Cloudy";

        // Display weather description
        mDescriptionView.setText(description);


        /* Temperature ************************************************************************** */

        // Read temperature from weather object
        String temperatureString = "17°";

        // Display high temperature
        mTempTextView.setText(temperatureString);


        /* High (max) & Low (min) temperature temperature *************************************** */

        // Read high temperature from weather object
        String highTemperatureString = "19°";

        // Read low temperature from weather object
        String lowTemperatureString = "10°";

        // Display high/low temperature
        mHighLowTempView.setText(getString(R.string.high_low_temperature, highTemperatureString, lowTemperatureString));

    }


}
