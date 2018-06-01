package com.ureview;

import android.support.multidex.MultiDexApplication;

public class BaseApplication extends MultiDexApplication {
    private static BaseApplication mInstance;

    public static synchronized BaseApplication getInstance() {
        if (mInstance == null) mInstance = new BaseApplication();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
