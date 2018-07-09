package com.dzh.coolweather.gson;

/**
 * Created by mocking on 2018/7/8.
 *
 *
 * "aqi" : {
 *     "city" : {
 *         "aqi" : "44",
 *         "pm25" : "13"
 *     }
 *  }
 */

public class AQI {

    public AQICity city;
    public class AQICity{

        public String aqi;

        public String pm25;
    }
}
