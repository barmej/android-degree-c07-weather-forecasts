package com.barmej.weatherforecasts;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.barmej.weatherforecasts.adapters.DaysForecastAdapter;
import com.barmej.weatherforecasts.adapters.HoursForecastAdapter;
import com.barmej.weatherforecasts.entity.ForecastLists;
import com.barmej.weatherforecasts.entity.WeatherInfo;
import com.barmej.weatherforecasts.fragments.PrimaryWeatherInfoFragment;
import com.barmej.weatherforecasts.fragments.SecondaryWeatherInfoFragment;
import com.barmej.weatherforecasts.network.NetworkUtils;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity that show current weather info, next hours & days forecasts
 */
public class MainActivity extends AppCompatActivity {

    /**
     * FragmentManager to be used in ViewPager FragmentAdapter
     */
    private FragmentManager mFragmentManager;

    /**
     * ViewPage & FragmentPagerAdapter to show the HeaderFragments
     */
    private HeaderFragmentAdapter mHeaderFragmentAdapter;
    private ViewPager mViewPager;

    /**
     * RecyclerViews & it's Adapters to show forecasts lists
     */
    private HoursForecastAdapter mHoursForecastAdapter;
    private DaysForecastAdapter mDaysForecastsAdapter;

    private RecyclerView mHoursForecastsRecyclerView;
    private RecyclerView mDaysForecastRecyclerView;

    /**
     * An instance of NetworkUtils for all network related operations
     */
    private NetworkUtils mNetworkUtils;

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

        // Get instance of NetworkUtils
        mNetworkUtils = NetworkUtils.getInstance(this);

        // Request current weather data
        requestWeatherInfo();

        // Request forecasts data
        requestForecastsInfo();
    }

    /**
     * Request current weather data
     */
    private void requestWeatherInfo() {

        // The getWeatherUrl method will return the URL that we need to get the JSON for the current weather
        String weatherRequestUrl = NetworkUtils.getWeatherUrl(this).toString();

        // Request a string response from the provided URL.
        JsonObjectRequest weatherInfoRequest = new JsonObjectRequest(Request.Method.GET, weatherRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WeatherInfo weatherInfo = null;
                        try {
                            // Get WeatherInfo object from json response
                            weatherInfo = OpenWeatherDataParser.getWeatherInfoObjectFromJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (weatherInfo != null) {
                            mHeaderFragmentAdapter.updateData(weatherInfo);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        mNetworkUtils.addToRequestQueue(weatherInfoRequest);
    }

    /**
     * Request forecasts data
     */
    private void requestForecastsInfo() {

        // The getForecastsUrl method will return the URL that we need to get the JSON for the upcoming forecasts
        String forecastsRequestUrl = NetworkUtils.getForecastUrl(MainActivity.this).toString();

        // Request a string response from the provided URL.
        JsonObjectRequest forecastsListRequest = new JsonObjectRequest(Request.Method.GET, forecastsRequestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ForecastLists forecastLists = null;
                        try {
                            // Get ForecastLists object from json response
                            forecastLists = OpenWeatherDataParser.getForecastsDataFromJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (forecastLists != null
                                && forecastLists.getHoursForecasts() != null
                                && forecastLists.getDaysForecasts() != null) {
                            mHoursForecastAdapter.updateData(forecastLists.getHoursForecasts());
                            mDaysForecastsAdapter.updateData(forecastLists.getDaysForecasts());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        mNetworkUtils.addToRequestQueue(forecastsListRequest);

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

        /**
         * Update data presented in the fragments of the ViewPager
         *
         * @param weatherInfo WeatherInfo object that contain the new data
         */
        void updateData(WeatherInfo weatherInfo) {
            ((PrimaryWeatherInfoFragment) fragments.get(0)).updateWeatherInfo(weatherInfo);
            ((SecondaryWeatherInfoFragment) fragments.get(1)).updateWeatherInfo(weatherInfo);
        }

    }


}
