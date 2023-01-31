package com.barmej.weatherforecasts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.barmej.weatherforecasts.adapters.DaysForecastAdapter;
import com.barmej.weatherforecasts.adapters.HoursForecastAdapter;
import com.barmej.weatherforecasts.entity.Forecast;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.fragments.PrimaryWeatherInfoFragment;
import com.barmej.weatherforecasts.fragments.SecondaryWeatherInfoFragment;
import com.barmej.weatherforecasts.network.NetworkUtils;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity that show current weather info, next hours & days forecasts
 */
public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private HeaderFragmentAdapter mHeaderFragmentAdapter;
    private ViewPager mViewPager;

    private HoursForecastAdapter mHoursForecastAdapter;
    private DaysForecastAdapter mDaysForecastsAdapter;

    private RecyclerView mHoursForecastsRecyclerView;
    private RecyclerView mDaysForecastRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of FragmentManager and assign it to mFragmentManager variable
        mFragmentManager = getSupportFragmentManager();

        // Create ViewPager instance
        mViewPager = findViewById(R.id.pager);

        // Create and attach HeaderFragmentAdapter to the ViewPager
        mHeaderFragmentAdapter = new HeaderFragmentAdapter(mFragmentManager);
        mViewPager.setAdapter(mHeaderFragmentAdapter);

        // Setup the TableLayout with the ViewPager
        TabLayout tabLayout = findViewById(R.id.indicator);
        tabLayout.setupWithViewPager(mViewPager, true);

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

        // Show forecasts lists
        mHoursForecastAdapter.updateData(hourForecasts);
        mDaysForecastsAdapter.updateData(daysForecasts);

        // Request current weather data
        requestWeatherInfo();

    }

    /**
     * Request current weather data
     */
    private void requestWeatherInfo() {
        new WeatherDataPullTask().execute();
    }

    /**
     * AsyncTask to execute data pulling request off the main thread
     */
    class WeatherDataPullTask extends AsyncTask<Void, Integer, WeatherInfo> {

        protected WeatherInfo doInBackground(Void... v) {

            // The getWeatherUrl method will return the URL that we need to get the JSON for the current weather
            URL weatherRequestUrl = NetworkUtils.getWeatherUrl(MainActivity.this);

            WeatherInfo weatherInfo;
            try {
                // Use the URL to retrieve the JSON
                String weatherJsonResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                // Get WeatherInfo object from json response
                weatherInfo = OpenWeatherDataParser.getWeatherInfoObjectFromJson(weatherJsonResponse);
                return weatherInfo;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(WeatherInfo weatherInfo) {
            Toast.makeText(MainActivity.this, "Request Operation Completed!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * FragmentPagerAdapter class to create and manage header fragments for the ViewPager
     */
    class HeaderFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        HeaderFragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new PrimaryWeatherInfoFragment();
                case 1:
                    return new SecondaryWeatherInfoFragment();
            }
            return null;
        }

        @Override
        public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {
            // Update the array list to refer to the instantiated fragments
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments.add(position,fragment);
            return fragment;
        }

    }




}
