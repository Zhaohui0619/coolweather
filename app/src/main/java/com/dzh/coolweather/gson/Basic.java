package com.dzh.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mocking on 2018/7/8.
 *
 * "basic" : {
 *     "city" : "苏州",
 *     "id" : "CN101190401",
 *     "update" : {
 *         "loc" : "2018-08-08 08:08"
 *     }
 * }
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{

        @SerializedName("loc")
        public String updateTime;
    }
}
