package io.humio.androidlogger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HumioLogger {
    private static String packageName;
    private static String versionCode;
    private static String versionName;

    private static int INTERVAL = 10000;
    private static int BUFFER_MAX = 20;
    private static Timer timer;
    private static Map<String, String> tags;
    private static boolean enableBulk = true;

    private static List<Event> eventBuffer = new ArrayList<>();

    public static void with(final Context context, final String URL, final String token) {
        BackEndClient.setupInstance(URL, token);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageName = pInfo.packageName;
            versionCode = String.valueOf(pInfo.versionCode);
            versionName = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (enableBulk) {
            if (timer == null) {
                timer = new Timer();
            }
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    checkBuffer(eventBuffer.size()>0);
                }
            };
            timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL);
        } else if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static void with(final Context context, final String URL, final String token, final boolean enable) {
        enableBulk = enable;
        with(context, URL, token);
    }

    static void sendLog(final String debugLevel, final String TAG, final String message) {
        final List<Event> events = new ArrayList<>();
        final Map<String, String> attributes = new HashMap<>();

        attributes.put("filename", getFileName());
        attributes.put("line", String.valueOf(getLineNumber()));
        attributes.put("logLevel", debugLevel);
        attributes.put("TAG", TAG);

        if (enableBulk) {
            eventBuffer.add(new Event(attributes,message));
            checkBuffer();
        } else {
            events.add(new Event(attributes,message));
            final List<IngestRequest> request = new ArrayList<>();
            request.add(new IngestRequest(getDefaultTags(), events));
            sendIngest(request);
        }
    }

    private static void sendIngest(final List<IngestRequest> ingestRequests){
        BackEndClient.getInstance().getService().ingest(ingestRequests).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) { }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // No internet connection - retry in 10 sec
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendIngest(ingestRequests);
                    }
                }, 10000);
            }
        });
    }

    private static Map<String, String> getDefaultTags(){
        if(tags  == null){
            tags = new HashMap<>();
            tags.put(HumioLoggerConfig.BUNDLE_IDENTIFIER_KEY, packageName);
            tags.put(HumioLoggerConfig.PLATFORM_KEY, HumioLoggerConfig.PLATFORM_VALUE);
            tags.put(HumioLoggerConfig.BUNDLE_SHORT_VERSION_STRING_KEY, versionName.replace(" ", ""));
            tags.put(HumioLoggerConfig.BUNDLE_VERSION_KEY, versionCode);
            tags.put(HumioLoggerConfig.SOURCE_KEY, HumioLoggerConfig.SOURCE_VALUE);
            tags.put(HumioLoggerConfig.DEVICE_NAME_KEY, Build.MODEL.replace(" ", ""));
        }
        return tags;
    }

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[5].getLineNumber();
    }

    private static String getFileName(){
        return Thread.currentThread().getStackTrace()[5].getFileName();
    }
    private static void checkBuffer() {
        checkBuffer(false);
    }

    private static void checkBuffer(final boolean forced) {
        final List<IngestRequest> request = new ArrayList<>();
        if (eventBuffer.size() >= BUFFER_MAX || forced) {
            request.add(new IngestRequest(getDefaultTags(), eventBuffer));
            sendIngest(request);
            eventBuffer.clear();
        }
    }
}