package com.example.mobilemeals.helpers;

import android.app.Application;
import android.content.Context;
public class GlobalApplication extends Application {
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        /* If you have other classes that need context object to initialize when application is created,
         you can use the appContext here to process. */
    }
    public static Context getAppContext() {
        return appContext;
    }
}