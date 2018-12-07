package com.example.a67024.coolweatherapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a67024.coolweatherapp.db.City;
import com.example.a67024.coolweatherapp.db.County;
import com.example.a67024.coolweatherapp.db.Province;
import com.example.a67024.coolweatherapp.util.HttpUtil;
import com.example.a67024.coolweatherapp.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    public static final String TYPE_PROVINCE = "province";
    public static final String TYPE_CITY = "city";
    public static final String TYPE_COUNTY = "county";

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    private static final String SERVER_ADDRESS = "http://guolin.tech/api/china";
    public ChooseAreaFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "ChooseAreaFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        backButton = view.findViewById(R.id.back_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                                android.R.color.holo_green_light, android.R.color.holo_red_light);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        titleText.setText("China");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province: provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            Log.d(TAG, "province address = " + TYPE_PROVINCE);
            queryFromServer(SERVER_ADDRESS, TYPE_PROVINCE);
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city: cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = SERVER_ADDRESS + "/" + provinceCode;
            Log.d(TAG, "city address = " + address);
            queryFromServer(address, TYPE_CITY);
        }
    }

    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county: countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = SERVER_ADDRESS + "/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            Log.d(TAG, "county address = " + address);
            queryFromServer(address, TYPE_COUNTY);
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDiaglog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeProgressDialog();
                mHandler.sendMessage(mHandler.obtainMessage(SHOW_NOTICE, "加载失败..."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse enter.");
                String responseStr = response.body().string();
                boolean result = false;
                Log.d(TAG, "onResponse enter. responseStr = " + responseStr);
                if (TYPE_PROVINCE.equalsIgnoreCase(type)) {
                    result = Utility.handleProvinceResponse(responseStr);
                } else if (TYPE_CITY.equalsIgnoreCase(type)) {
                    result = Utility.handleCityResponse(responseStr, selectedProvince.getId());
                } else if (TYPE_COUNTY.equalsIgnoreCase(type)) {
                    result = Utility.handleCountyResponse(responseStr, selectedCity.getId());
                } else {
                    Log.d(TAG, "onResponse = " + type);
                }
                Log.d(TAG, "onResponse type = " + type + " result = " + result);
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TYPE_PROVINCE.equalsIgnoreCase(type)) {
                                queryProvinces();
                            }else if (TYPE_CITY.equalsIgnoreCase(type)) {
                                queryCities();
                            } else if (TYPE_COUNTY.equalsIgnoreCase(type)) {
                                queryCounties();
                            }
                        }
                    });
                } else {
                    mHandler.sendMessage(mHandler.obtainMessage(SHOW_NOTICE, "has no city or county."));
                }
                closeProgressDialog();
            }
        });
    }

    private static final int SHOW_NOTICE = 1;
    private Handler mHandler = new Handler() {
         @Override
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case SHOW_NOTICE:
                    showToast((String) msg.obj);
                    break;
                default:
                    Log.d(TAG, "type = " + type);
                    break;
            }
        }
    };


    private void showProgressDiaglog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        if (message != null) {
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout layout = (LinearLayout) toast.getView();
            TextView tv_msg = layout.findViewById(android.R.id.message);
            ImageView image = new ImageView(getContext());
            image.setImageResource(R.mipmap.warning_pic);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            layout.addView(image, 0);
            tv_msg.setTextColor(Color.BLUE);
            toast.show();
        }
    }
}
