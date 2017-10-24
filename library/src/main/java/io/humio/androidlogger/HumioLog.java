package io.humio.androidlogger;

public class HumioLog {
    public static void v(final String message) {
        HumioLogger.sendLog("VERBOSE", message);
    }

    public static void d(final String message) {
        HumioLogger.sendLog("DEBUG", message);
    }

    public static void i(final String message) {
        HumioLogger.sendLog("INFO", message);
    }

    public static void w(final String message) {
        HumioLogger.sendLog("WARN", message);
    }

    public static void e(final String message) {
        HumioLogger.sendLog("ERROR", message);
    }
}