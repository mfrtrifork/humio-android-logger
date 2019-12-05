package io.humio.androidlogger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.humio.androidlogger.models.Event;
import io.humio.androidlogger.models.IngestRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HumioLogger {
    private static final String TAG = "HumioLogger";
    private static String packageName;
    private static String versionCode;
    private static String versionName;

    private static int INTERVAL = 10000;
    private static int BUFFER_MAX = 20;
    private static Timer timer;
    private static Map<String, String> tags;
    private static Map<String, String> attributes;
    private static Map<String, String> additionalAttr;

    private static boolean enableBulk = true;
    private static String loggerId = UUID.randomUUID().toString();

    private static final HandlerThread mHandlerThread = new HandlerThread("HandlerThread");


    private static List<Event> eventBuffer = new ArrayList<>();
    private static Handler mHandler;

    public static void with(final Context context, final String URL, final String token, final boolean enableRequestLogging) {
        with(context, URL, token, enableRequestLogging, enableBulk);
    }

    public static void with(final Context context, final String URL, final String token, final boolean enableRequestLogging, final boolean enableBulking) {
        with(context, URL, token, enableRequestLogging, enableBulking, null);
    }

    public static void with(final Context context, final String URL, final String token, final boolean enableRequestLogging, final boolean enableBulking, Map<String, String> additionalAttributes) {
        BackEndClient.setupInstance(URL, token, enableRequestLogging);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            packageName = pInfo.packageName;
            versionCode = String.valueOf(pInfo.versionCode);
            versionName = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        enableBulk = enableBulking;
        if (enableBulk) {
            if (timer == null) {
                timer = new Timer();
            }
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    checkBuffer(eventBuffer.size() > 0);
                }
            };
            timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL);
        } else if (timer != null) {
            timer.cancel();
            timer = null;
        }
        additionalAttr = additionalAttributes;
    }
    
    private static Handler getHandler() {
        if (mHandler == null) {
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        return mHandler;
    }

    static synchronized void sendLog(final String logLevel, final String message) {
        try {
            final Event event = new Event(getAttributes(), getFormattedRawString(logLevel, message));
            if (enableBulk) {
                getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            eventBuffer.add(event);
                        }
                    }
                });
                checkBuffer();
            } else {
                List<Event> events = Collections.singletonList(event);
                sendIngest(Collections.singletonList(new IngestRequest(getDefaultTags(), events)));
            }
        } catch (final ArrayIndexOutOfBoundsException ignored) {

        }
    }

    private static String getFormattedRawString(String logLevel, String message) {
        String result = message + " ";
        if (!"HumioLog.java".equals(getFileName())) {
            result += "filename='" + getFileName() + "' ";
            result += "line=" + getLineNumber() + " ";
        }
        if (logLevel != null) {
            result += "logLevel=" + logLevel + " ";
        }
        return result;
    }

    private static Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
            attributes.put(HumioLoggerConfig.LOGGER_ID_KEY, loggerId);
            attributes.put(HumioLoggerConfig.BUNDLE_VERSION_KEY, versionCode);
            if (versionName != null) {
                attributes.put(HumioLoggerConfig.BUNDLE_SHORT_VERSION_KEY, versionName.replace(" ", ""));
            }
            attributes.put(HumioLoggerConfig.OS_VERSION_KEY, String.valueOf(Build.VERSION.SDK_INT));
            attributes.put(HumioLoggerConfig.MANUFACTURER_KEY, String.valueOf(Build.MANUFACTURER));
            attributes.put(HumioLoggerConfig.BRAND_KEY, String.valueOf(Build.BRAND));
            attributes.put(HumioLoggerConfig.DEVICE_KEY, String.valueOf(Build.DEVICE));
            attributes.put(HumioLoggerConfig.MODEL_KEY, String.valueOf(Build.MODEL));
            attributes.put(HumioLoggerConfig.PRODUCT_KEY, String.valueOf(Build.PRODUCT));
            if (additionalAttr != null) {
                attributes.putAll(additionalAttr);
            }
        }
        return attributes;
    }


    private static void sendIngest(final List<IngestRequest> ingestRequests) {
        BackEndClient instance = BackEndClient.getInstance();
        if (instance != null) {
            instance.getService().ingest(ingestRequests).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                }

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
        } else {
            Log.e(TAG, "Can't log when Humio not set up!");
        }
    }

    private static Map<String, String> getDefaultTags() {
        if (tags == null) {
            tags = new HashMap<>();
            tags.put(HumioLoggerConfig.PLATFORM_KEY, HumioLoggerConfig.PLATFORM_VALUE);
            tags.put(HumioLoggerConfig.BUNDLE_IDENTIFIER_KEY, packageName);
            tags.put(HumioLoggerConfig.SOURCE_KEY, HumioLoggerConfig.SOURCE_VALUE);
        }
        return tags;
    }

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[5].getLineNumber();
    }

    private static String getFileName() {
        return Thread.currentThread().getStackTrace()[5].getFileName();
    }

    private static void checkBuffer() {
        checkBuffer(false);
    }

    private static void checkBuffer(final boolean forced) {
        final List<IngestRequest> request = new ArrayList<>();
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (eventBuffer.size() >= BUFFER_MAX || forced) {
                        request.add(new IngestRequest(getDefaultTags(), eventBuffer));
                        sendIngest(request);
                        eventBuffer.clear();
                    }
                }
            }
        });
    }
}