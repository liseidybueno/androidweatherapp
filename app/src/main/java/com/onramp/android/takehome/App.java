package com.onramp.android.takehome;

import android.app.Application;
import android.content.res.Resources;

public class App extends Application {

    private static App instance;
    private static Resources resource;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        resource = getResources();
    }

    public static App getInstance() {
        return instance;
    }

    public static Resources getResource() {
        return resource;
    }


}
