package io.humio.humiotest;

import android.app.Application;

import io.humio.androidlogger.HumioLogger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HumioLogger.with(this, "https://driver-tracking-app.humio.com/api/v1/dataspaces/driver-tracking-app/", "OauPG1UDwUxkfwIDBH8mBIM0qLQIqiKi14TFAgKou5FU", true);
    }
}