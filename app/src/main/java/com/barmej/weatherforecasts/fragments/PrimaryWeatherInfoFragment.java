package com.barmej.weatherforecasts.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.utils.CustomDateUtils;
import com.barmej.weatherforecasts.utils.WeatherUtils;

/**
 * A fragment that show primary weather information like high and low temperatures, weather icon,
 * weather description, today's date and current city.
 * You can create an instance of this fragment and embed or add to other activity or fragment!
 */
public class PrimaryWeatherInfoFragment extends Fragment {

    private ImageView mIconImageView;
    private TextView mCityNameTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mTemperatureTextView;
    private TextView mHighLowTempTextView;

    private WeatherInfo mWeatherInfo;

    /**
     * Required empty public constructor
     */
    public PrimaryWeatherInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_primary_weather_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mainView = getView();

        if(mainView == null) return;

        // Initialize member variables
        mIconImageView = mainView.findViewById(R.id.weather_icon);
        mCityNameTextView = mainView.findViewById(R.id.city);
        mDateTextView = mainView.findViewById(R.id.date);
        mDescriptionTextView = mainView.findViewById(R.id.weather_description);
        mTemperatureTextView = mainView.findViewById(R.id.temperature);
        mHighLowTempTextView = mainView.findViewById(R.id.high_low_temperature);

        // Show current weather info
        showWeatherInfo();

    }

    /**
     * Update weather info object and reflect the updated data on UI
     *
     * @param weatherInfo WeatherInfo object that contain the new data
     */
    public void updateWeatherInfo(WeatherInfo weatherInfo) {
        mWeatherInfo = weatherInfo;
        showWeatherInfo();
    }

    /**
     * This method used to show current weather info inside user interface views
     */
    private void showWeatherInfo() {

        if (mWeatherInfo == null) {
            return;
        }

        /* Weather Icon ************************************************************************* */

        // Get the weather icon resource id based on icon string passed from the api
        int weatherImageId = WeatherUtils.getWeatherIcon(mWeatherInfo.getWeather().get(0).getIcon());

        // Display weather condition icon
        mIconImageView.setImageResource(weatherImageId);

        /* Current city ************************************************************************* */

        // Read date from weather info object
        String cityName = mWeatherInfo.getName();

        // Display city name
        mCityNameTextView.setText(cityName);

        /* Weather Date ************************************************************************* */

        // Get human readable string using getFriendlyDateString utility method and display it
        String dateString = CustomDateUtils.getFriendlyDateString(getContext(), mWeatherInfo.getDt(), false);

        /* Display friendly date string */
        mDateTextView.setText(dateString);

        /* Weather Description ****************************************************************** */

        // Get weather condition description
        String description = mWeatherInfo.getWeather().get(0).getDescription();

        // Display weather description
        mDescriptionTextView.setText(description);

        /* Temperature ************************************************************************** */

        // Read temperature from weather object
        String temperatureString = getString(R.string.format_temperature, mWeatherInfo.getMain().getTemp());

        // Display high temperature
        mTemperatureTextView.setText(temperatureString);

        /* High (max) & Low (min) temperature temperature *************************************** */

        // Read high temperature from weather object
        double highTemperature = mWeatherInfo.getMain().getTempMax();

        // Read low temperature from weather object
        double lowTemperature = mWeatherInfo.getMain().getTempMin();

        // Display high/low temperature
        mHighLowTempTextView.setText(getString(R.string.high_low_temperature, highTemperature, lowTemperature));

    }


}
