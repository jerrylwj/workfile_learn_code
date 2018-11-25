package com.example.a67024.coolweatherapp.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    public String getWeatherId() {
        if (basic != null) {
            return basic.weatherId;
        }
        return null;
    }
}
