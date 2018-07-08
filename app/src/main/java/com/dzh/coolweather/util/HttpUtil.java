package com.dzh.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by mocking on 2018/7/8.
 *
 * 网络请求 工具类
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String url, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
