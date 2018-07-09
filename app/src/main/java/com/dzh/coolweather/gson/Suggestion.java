package com.dzh.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mocking on 2018/7/8.
 *
 * "suggestion" : {
 *     "comf" : {
 *         "txt" : "白天天气较热..."
 *     },
 *     "cw" : {
 *         "txt" : "不宜洗车..."
 *     },
 *     "sport" : {
 *         "txt" ; "不宜进行室外运动.."
 *     }
 * }
 *
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;
    }

    public class CarWash{

        @SerializedName("txt")
        public String info;
    }

    public class Sport{

        @SerializedName("txt")
        public String info;
    }
}
