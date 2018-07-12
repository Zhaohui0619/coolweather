package com.dzh.coolweather.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dzh.coolweather.R;
import com.dzh.coolweather.gson.Forecast;
import com.dzh.coolweather.gson.Weather;
import com.dzh.coolweather.util.HttpUtil;
import com.dzh.coolweather.util.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    @BindView(R.id.sv_weather)
    ScrollView weather_sv;

    @BindView(R.id.tv_title_city)
    TextView titleCity_tv;

    @BindView(R.id.tv_title_time)
    TextView titleTime_tv;

    @BindView(R.id.tv_degree_info)
    TextView degreeInfo_tv;

    @BindView(R.id.tv_weather_info)
    TextView weatherInfo_tv;

    @BindView(R.id.lv_forecast)
    LinearLayout forecast_layout;

    @BindView(R.id.tv_aqi_info)
    TextView aqiInfo_tv;

    @BindView(R.id.tv_pm25_info)
    TextView pmInfo_tv;

    @BindView(R.id.tv_comfort_info)
    TextView comfortInfo_tv;

    @BindView(R.id.tv_wash_info)
    TextView carWashInfo_tv;

    @BindView(R.id.tv_sport_info)
    TextView sportInfo_tv;

    @BindView(R.id.iv_bing_image)
    ImageView bingImage_iv;

//    @BindView(R.id.srl_weather)
//    SwipeRefreshLayout refreshLayout;

    public SwipeRefreshLayout refreshLayout;

//    @BindView(R.id.dl_area)
//    DrawerLayout area_layout;
    public DrawerLayout area_drawerLayout;

    @BindView(R.id.btn_navigation)
    Button navigation_btn;

    private String mWeatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        area_drawerLayout = findViewById(R.id.dl_area);
        refreshLayout = findViewById(R.id.srl_weather);

        //Android 5.0 以上的系统支持将背景图和状态栏融入到一起
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            //将活动的布局显示到状态栏上边
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //手动刷新天气信息
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherContent = preferences.getString("weather",null);
        if (weatherContent!=null){
            //若有本地缓存数据则直接进行处理
            Weather weather = Utility.handleWeatherResponse(weatherContent);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            //若无本地缓存数据则到服务器获取天气数据并处理
            String weatherId = getIntent().getStringExtra("weatherId");
            mWeatherId = weatherId;
            weather_sv.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String bingImage = preferences.getString("bingImage",null);
        if (bingImage != null){
            //若有本地图片缓存则直接加载
            Glide.with(this).load(bingImage).into(bingImage_iv);
        }else {
            //若无本地图片缓存则到服务器获取图片并加载
            loadBingImage();
        }

        navigation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                area_drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 从服务器获取天气信息
     * @param weatherId 天气 Id
     */
    public void requestWeather(String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=6d5a151aa1904e1e9523e9c541bbf23c";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT)
                                .show();
                        refreshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherResponse = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(weatherResponse);

                //回到主线程中来完成UI绘制以及响应用户的操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",weatherResponse);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT)
                                    .show();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });

            }
        });
        loadBingImage();
    }

    /**
     * 处理并展示装配后的 weather 对象中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degreeInfo = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity_tv.setText(cityName);
        titleTime_tv.setText(updateTime);
        degreeInfo_tv.setText(degreeInfo);
        weatherInfo_tv.setText(weatherInfo);

        forecast_layout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.item_forecast,forecast_layout,false);
            TextView date_tv = view.findViewById(R.id.tv_forecast_date);
            TextView info_tv = view.findViewById(R.id.tv_forecast_info);
            TextView max_tv = view.findViewById(R.id.tv_forecast_max);
            TextView min_tv = view.findViewById(R.id.tv_forecast_min);

            date_tv.setText(forecast.date);
            info_tv.setText(forecast.more.info);
            max_tv.setText(forecast.temperature.max);
            min_tv.setText(forecast.temperature.min);
            forecast_layout.addView(view);
        }

        if (weather.aqi != null){
            aqiInfo_tv.setText(weather.aqi.city.aqi);
            pmInfo_tv.setText(weather.aqi.city.pm25);
        }

        String comfortInfo = "舒适度：" + weather.suggestion.comfort.info;
        String carWashInfo = "洗车指数：" + weather.suggestion.carWash.info;
        String sportInfo = "运动建议：" + weather.suggestion.sport.info;
        comfortInfo_tv.setText(comfortInfo);
        carWashInfo_tv.setText(carWashInfo);
        sportInfo_tv.setText(sportInfo);
        weather_sv.setVisibility(View.VISIBLE);

    }

    private void loadBingImage(){
        String requestImageUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestImageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingImage = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bingImage",bingImage);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingImage).into(bingImage_iv);
                    }
                });
            }
        });
    }
}
