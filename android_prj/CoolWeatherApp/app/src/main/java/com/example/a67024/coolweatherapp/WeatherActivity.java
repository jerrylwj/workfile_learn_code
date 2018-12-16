package com.example.a67024.coolweatherapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a67024.coolweatherapp.gson.Forecast;
import com.example.a67024.coolweatherapp.gson.Weather;
import com.example.a67024.coolweatherapp.util.HttpUtil;
import com.example.a67024.coolweatherapp.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeTxt;

    private TextView weatherInfoTxt;

    private LinearLayout forecastLayout;

    private TextView aqiTxt;

    private TextView pm25Txt;

    private TextView comfortTxt;

    private TextView carWashTxt;

    private ImageView bing_img;

    private TextView sportTxt;

    public SwipeRefreshLayout swipeRefresh;
    private String weather_id;

    public DrawerLayout drawerLayout;
    private Button navBtn;

    public static final String WEATHER_URL = "http://guolin.tech/api/weather?cityid=";
    public static final String KEY_URL = "2eae25e74f36404ab4b36389f7940eb1";
    private static final String IMG_URL = "http://guolin.tech/api/bing_pic";
    private static final String SP_BING_PIC = "bing_pic";
    private static final String SP_WEATHER = "weather";
    private static final String TAG = "WeatherActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * 状态栏和背景图片融合在一体，作为布局的一部分，布局会上移动，如果设置了fitsSystemWindows,则布局位置不变
        * */

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeTxt = findViewById(R.id.degree_txt);
        weatherInfoTxt = findViewById(R.id.weather_info_txt);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiTxt = findViewById(R.id.aqi_txt);
        pm25Txt = findViewById(R.id.pm25_txt);
        comfortTxt = findViewById(R.id.comfort_txt);
        carWashTxt = findViewById(R.id.car_wash_txt);
        sportTxt = findViewById(R.id.sport_txt);
        bing_img = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navBtn = findViewById(R.id.nav_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString(SP_WEATHER, null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weather_id = weather.getWeatherId();
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weather_id);
            }
        });

        String bingPic = sp.getString(SP_BING_PIC, null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bing_img);
        } else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        HttpUtil.sendOkHttpRequest(IMG_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SP_BING_PIC, bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bing_img);
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = WEATHER_URL + weatherId + "&key=" + KEY_URL;
        Log.d(TAG,"requestWeather weatherUrl = " + weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseTxt = response.body().string();
                Log.d(TAG, "onResponse, responseText = " + responseTxt);
                final Weather weather = Utility.handleWeatherResponse(responseTxt);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).
                                    edit();
                            editor.putString(SP_WEATHER, responseTxt);
                            editor.apply();
                            weather_id = weather.getWeatherId();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo =weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeTxt.setText(degree);
        weatherInfoTxt.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for (Forecast forecast: weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_txt);
            TextView info = view.findViewById(R.id.info_txt);
            TextView maxTxt = view.findViewById(R.id.max_txt);
            TextView minTxt = view.findViewById(R.id.min_txt);
            dateText.setText(forecast.date);
            info.setText(forecast.more.info);
            maxTxt.setText(forecast.temperature.max);
            minTxt.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiTxt.setText(weather.aqi.city.aqi);
            pm25Txt.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " +weather.suggestion.carWash.info;
        String sport = "运动指数: " + weather.suggestion.sport.info;
        comfortTxt.setText(comfort);
        carWashTxt.setText(carWash);
        sportTxt.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }


}
