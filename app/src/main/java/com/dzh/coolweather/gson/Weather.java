package com.dzh.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mocking on 2018/7/8.
 *
 * {
 *     "HeWeather" : [
 *          {
 *              "status" : "ok",
 *              "basic : {},
 *              "aqi" : {},
 *              "now" : {},
 *              "suggestion" : {},
 *              "daily_forecast" : []
 *          }
 *      ]
 * }
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
