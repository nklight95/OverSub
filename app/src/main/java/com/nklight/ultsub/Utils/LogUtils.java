package com.nklight.ultsub.Utils;

import android.util.Log;

import com.nklight.ultsub.BuildConfig;

public class LogUtils {
    public static void d(String tag, String log) {
        if (BuildConfig.isLogEnable) {
            Log.d(tag, log);
        }
    }

    public static void e(String tag, String log) {
        if (BuildConfig.isLogEnable) {
            Log.e(tag, log);
        }
    }

    public static void e(String tag, String log, Throwable throwable) {
        if (BuildConfig.isLogEnable) {
            Log.e(tag, log, throwable);
        }
    }
}
