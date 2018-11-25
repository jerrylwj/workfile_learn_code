package com.example.a67024.coolweatherapp.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.a67024.coolweatherapp.db.City;
import com.example.a67024.coolweatherapp.db.County;
import com.example.a67024.coolweatherapp.db.Province;
import com.example.a67024.coolweatherapp.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    private static final String TAG = "Utility";
    public static boolean handleProvinceResponse(String reponse) {
        if (!TextUtils.isEmpty(reponse)) {
            try {
                JSONArray provinceArray = new JSONArray(reponse);
                if (provinceArray.length() == 0) {
                    return false;
                }
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObj = provinceArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObj.getInt("id"));
                    province.setProvinceName(provinceObj.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                Log.e(TAG, "exception e = " + e);
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        Log.d(TAG, "response = " + response + " provinceId = " + provinceId);
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArray = new JSONArray(response);
                if (cityArray.length() == 0) {
                    return false;
                }
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cityObj = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(cityObj.getString("name"));
                    city.setCityCode(cityObj.getInt("id"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                Thread.dumpStack();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countyArray = new JSONArray(response);
                int len = countyArray.length();
                if (len == 0) {
                    return false;
                }
                for (int i = 0; i < len; i++) {
                    JSONObject countyObject = countyArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                Thread.dumpStack();
            }
        }
        return false;
    }

    /*
    * 将返回的GSON数据解析成Weather实体类
    * */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.d(TAG, "handleWeatherResponse, weatherContent=" + weatherContent);
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
