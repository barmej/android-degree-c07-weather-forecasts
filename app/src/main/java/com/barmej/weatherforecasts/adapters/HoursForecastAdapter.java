package com.barmej.weatherforecasts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.entity.Forecast;
import com.barmej.weatherforecasts.utils.CustomDateUtils;
import com.barmej.weatherforecasts.utils.WeatherUtils;

import java.util.List;

/**
 * {@link HoursForecastAdapter} exposes a list contains the next 24hrs weather forecasts
 * from a {@link List<Forecast>} to a {@link RecyclerView}.
 */
public class HoursForecastAdapter extends RecyclerView.Adapter<HoursForecastAdapter.ForecastAdapterViewHolder> {

    /**
     * The context to access app resources and inflate layouts
     */
    private final Context mContext;

    /**
     * List of next 24hrs forecasts
     */
    private List<Forecast> mForecasts;


    /**
     * HoursForecastAdapter constructor
     *
     * @param context Used to access the the UI and app resources
     */
    public HoursForecastAdapter(@NonNull Context context) {
        mContext = context;
    }

    /**
     * This method called when the RecyclerView is presented.
     * Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that will contain the inflated View
     * @param viewType  If your RecyclerView has more than one type of item you
     *                  can use this viewType integer to provide a different layout.
     *                  Check {@link RecyclerView.Adapter#getItemViewType(int)} for more details.
     * @return A new ForecastAdapterViewHolder that holds the list item view
     */
    @Override
    public @NonNull ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hour_forecast, viewGroup, false);
        return new ForecastAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {

        Forecast forecast = mForecasts.get(position);

        /* Weather Icon ************************************************************************* */

        // Get the weather icon resource id based on icon string passed from the api
        int weatherImageId = WeatherUtils.getWeatherIcon(forecast.getWeather().get(0).getIcon());

        // Display weather condition icon
        forecastAdapterViewHolder.iconImageView.setImageResource(weatherImageId);

        /* Weather Clock Time ******************************************************************* */

        // Get human readable string using getHourOfDayUTCTime utility method and display it
        String hourClockString = CustomDateUtils.getHourOfDayUTCTime(forecast.getDt());

        // Display clock hour
        forecastAdapterViewHolder.timeTextView.setText(hourClockString);

        /* High (max) temperature *************************************************************** */

        // Read high temperature from forecast object
        double highTemperature = forecast.getMain().getTempMax();

        // Get formatted high temperature string
        String highTemperatureString = mContext.getString(R.string.format_temperature, highTemperature);

        // Display high temperature
        forecastAdapterViewHolder.temperatureTextView.setText(highTemperatureString);

    }

    /**
     * This method simply returns the number of items to display.
     *
     * @return The number of items available to display
     */
    @Override
    public int getItemCount() {
        if (mForecasts == null) {
            return 0;
        } else {
            return mForecasts.size();
        }
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a list item.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView iconImageView;
        final TextView timeTextView;
        final TextView temperatureTextView;

        ForecastAdapterViewHolder(View view) {
            super(view);
            iconImageView = view.findViewById(R.id.weather_icon);
            timeTextView = view.findViewById(R.id.time);
            temperatureTextView = view.findViewById(R.id.temperature);
        }

    }

    /**
     * Update the current forecasts data with new list
     *
     * @param forecasts a list of {@link Forecast}
     */
    public void updateData(List<Forecast> forecasts) {
        this.mForecasts = forecasts;
        notifyDataSetChanged();
    }

}