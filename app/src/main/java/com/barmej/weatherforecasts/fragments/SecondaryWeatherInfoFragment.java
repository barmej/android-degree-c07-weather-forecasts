package com.barmej.weatherforecasts.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.utils.WeatherUtils;

/**
 * A fragment that show extra weather information like humidity, pressure, wind speed and direction
 * You can create an instance of this fragment and embed or add to other activity or fragment!
 */
public class SecondaryWeatherInfoFragment extends Fragment {

    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView windTextView;

    private WeatherInfo mWeatherInfo;

    /**
     * Required empty public constructor
     */
    public SecondaryWeatherInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_secondary_weather_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mainView = getView();

        if (mainView == null) return;

        // Initialize member variables
        humidityTextView = mainView.findViewById(R.id.humidity);
        pressureTextView = mainView.findViewById(R.id.pressure);
        windTextView = mainView.findViewById(R.id.wind_measurement);

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

        /* Humidity ***************************************************************************** */

        // Read humidity from weather object
        float humidity = mWeatherInfo.getMain().getHumidity();

        // Append % symbol to the humidity value and get it as a String
        String humidityString = getString(R.string.format_humidity, humidity);

        // Display the humidity text
        humidityTextView.setText(humidityString);

        /* Wind speed and direction ************************************************************* */

        // Read wind speed & direction from weather object
        double windSpeed = mWeatherInfo.getWind().getSpeed();
        double windDirection = mWeatherInfo.getWind().getDeg();

        // Get formatted wind speed & direction text
        String windString = WeatherUtils.getFormattedWind(getContext(), windSpeed, windDirection);

        // Display wind speed & direction text
        windTextView.setText(windString);

        /* Pressure ***************************************************************************** */

        // Read pressure from weather object
        double pressure = mWeatherInfo.getMain().getPressure();

        // Append pressure unit to the pressure value and return it as a String
        String pressureString = getString(R.string.format_pressure, pressure);

        // Display the pressure text
        pressureTextView.setText(pressureString);

    }

}
