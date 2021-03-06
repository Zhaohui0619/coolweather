package com.dzh.coolweather.util;

import android.text.TextUtils;

import com.dzh.coolweather.bean.City;
import com.dzh.coolweather.bean.County;
import com.dzh.coolweather.bean.Province;
import com.dzh.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mocking on 2018/7/8.
 */

public class Utility {

    /**
     * 解析并存储服务器返回的省级数据
     *
     * @param response 请求服务器返回的数据
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            JSONArray allProvinces = null;
            try {
                allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器返回的市级数据
     *
     * @param response   请求服务器返回的市级数据
     * @param provinceId 省Id
     * @return
     */
    public static boolean handleCityResponse(String response, Integer provinceId) {
        if (!TextUtils.isEmpty(response)) {
            JSONArray allCities = null;
            try {
                allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器返回的县、区级数据
     *
     * @param response 请求服务器返回的县、区级数据
     * @param cityId   市Id
     * @return
     */
    public static boolean handleCountyResponse(String response, Integer cityId) {
        if (!TextUtils.isEmpty(response)) {
            JSONArray allCounties = null;
            try {
                allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析处理从服务器获取的天气数据，将其装配称 Weather 对象
     *
     * @param response 从服务器获取的天气数据
     * @return
     */
    public static Weather handleWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherContent, Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
