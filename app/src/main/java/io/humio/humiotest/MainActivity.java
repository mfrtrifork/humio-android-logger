package io.humio.humiotest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.humio.androidlogger.HumioLog;
import io.humio.androidlogger.HumioLogger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HumioLogger.with(this, "https://YOUR_HUMIO_INSTANCE.humio.com/api/v1/dataspaces/YOUR_DATA_SPACE/", "YOUR API TOKEN");
    }

    public void sendTest(View view) {
        HumioLog.d(TAG, "Test");
    }
}