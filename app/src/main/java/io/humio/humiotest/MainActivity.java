package io.humio.humiotest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.humio.androidlogger.HumioLog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendTest(View view) {
        HumioLog.d(TAG, "Test");
    }
}