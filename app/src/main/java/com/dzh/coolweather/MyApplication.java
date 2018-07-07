package com.dzh.coolweather;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.litepal.LitePal;

/**
 * Created by mocking on 2018/7/7.
 *
 *  一般而言 MyApplication 中可以放置一些整个APP内使用的全局性对象，例如 全局的 Context
 *  修改清单文件，添加 <application>android:name=".MyApplication"</application>
 *
 */

public class MyApplication extends MultiDexApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //将全局Context 传给 LitePal
        LitePal.initialize(context);
    }

    public static Context getContext(){
        return context;
    }
}
