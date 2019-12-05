package io.humio.humiotest;

import android.app.Application;

import io.humio.androidlogger.HumioLogger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HumioLogger.with(this, "URL", "TOKEN", true);
    }
}