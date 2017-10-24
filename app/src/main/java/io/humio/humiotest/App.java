package io.humio.humiotest;

import android.app.Application;

import io.humio.androidlogger.HumioLogger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HumioLogger.with(this, "https://go.humio.com/api/v1/dataspaces/driver/", "WpNmX1WU2buOOO44qJq49kFklkAQkexZ", false);
    }
}