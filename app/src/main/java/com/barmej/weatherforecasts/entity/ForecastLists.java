package com.barmej.weatherforecasts.entity;

import java.util.List;

public class ForecastLists {

    private List<Forecast> hoursForecasts = null;
    private List<List<Forecast>> daysForecasts = null;

    public List<Forecast> getHoursForecasts() {
        return hoursForecasts;
    }

    public void setHoursForecasts(List<Forecast> hoursForecasts) {
        this.hoursForecasts = hoursForecasts;
    }

    public List<List<Forecast>> getDaysForecasts() {
        return daysForecasts;
    }

    public void setDaysForecasts(List<List<Forecast>> daysForecasts) {
        this.daysForecasts = daysForecasts;
    }

}