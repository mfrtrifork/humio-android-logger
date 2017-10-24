package io.humio.androidlogger;

public class HumioLog {

    public static void v(final String message){
        v(null, message);
    }

    public static void v(final String TAG, final String message) {
        HumioLogger.sendLog("VERBOSE", TAG, message);
    }

    public static void d(final String message){
        d(null, message);
    }

    public static void d(final String TAG, final String message) {
        HumioLogger.sendLog("DEBUG", TAG, message);
    }

    public static void i(final String message){
        i(null, message);
    }

    public static void i(final String TAG, final String message) {
        HumioLogger.sendLog("INFO", TAG, message);
    }

    public static void w(final String message){
        w(null, message);
    }

    public static void w(final String TAG, final String message) {
        HumioLogger.sendLog("WARN", TAG, message);
    }

    public static void e(final String message){
        e(null, message);
    }

    public static void e(final String TAG, final String message) {
        HumioLogger.sendLog("ERROR", TAG, message);
    }
}