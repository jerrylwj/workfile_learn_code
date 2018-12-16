package com.example.a67024.coolweatherapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.a67024.coolweatherapp.WeatherActivity;
import com.example.a67024.coolweatherapp.gson.Weather;
import com.example.a67024.coolweatherapp.util.Utility;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    }

    /*
    * 更新天气
    *
    * */
    private void updateWeather() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = sp.getString("weather", null);
        if (weatherStr != null) {
            Weather weather = Utility.handleWeatherResponse(weatherStr);
            String weatherId = weather.getWeatherId();
            String weatherUrl = WeatherActivity.WEATHER_URL + weatherId + "&key=" + WeatherActivity.KEY_URL;

        }
    }
}
