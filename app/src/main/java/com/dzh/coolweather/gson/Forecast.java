package com.dzh.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mocking on 2018/7/8.
 *
 * "daily_forecast" : [
 *      {
 *          "date" : "2018-08-08",
 *          "cond" : {
 *              "txt_d" : "阵雨"
 *          },
 *          "tmp" : {
 *              "max" : "34",
 *              "min" : "27"
 *          }
 *      },
 *      {
 *          "date" : "2018-08-08",
 *          "cond" : {
 *              "txt_d" : "阵雨"
 *          },
 *          "tmp" : {
 *              "max" : "34",
 *              "min" : "27"
 *          }
 *      },
 *      ...
 *      ]
 *
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More{

        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature{

        public String max;

        public String min;
    }
}
