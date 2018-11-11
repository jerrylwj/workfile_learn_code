package com.example.a67024.coolweatherapp.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.a67024.coolweatherapp.db.City;
import com.example.a67024.coolweatherapp.db.County;
import com.example.a67024.coolweatherapp.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    private static final String TAG = "Utility";
    public static boolean handleProvinceResponse(String reponse) {
        if (!TextUtils.isEmpty(reponse)) {
            try {
                JSONArray provinceArray = new JSONArray(reponse);
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
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArray = new JSONArray(response);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cityObj = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(cityObj.getString("name"));
                    city.setId(cityObj.getInt("id"));
                    city.save();
                }
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
                for (int i = 0; i < countyArray.length(); i++) {
                    JSONObject countyObject = countyArray.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }
            } catch (JSONException e) {
                Thread.dumpStack();
            }
        }
        return false;
    }
}
