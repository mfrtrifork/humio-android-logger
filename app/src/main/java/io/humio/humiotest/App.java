package io.humio.humiotest;

import android.app.Application;

import io.humio.androidlogger.HumioLogger;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HumioLogger.with(this, "https://YOUR_HUMIO_INSTANCE.humio.com/api/v1/dataspaces/YOUR_DATA_SPACE/", "YOUR API TOKEN");
    }
}