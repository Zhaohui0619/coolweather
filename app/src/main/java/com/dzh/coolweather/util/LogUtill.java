package com.dzh.coolweather.util;

import android.util.Log;

/**
 * Created by mocking on 2018/7/8.
 * <p>
 * 打印日志 工具类
 */

public class LogUtill {

    public static final Integer VERBOSE = 1;

    public static final Integer DEBUG = 2;

    public static final Integer INFO = 3;

    public static final Integer WARN = 4;

    public static final Integer ERROR = 5;

    public static final Integer NOTHING = 6;

    public static Integer level = VERBOSE;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level < ERROR) {
            Log.e(tag, msg);
        }
    }
}
