package com.nklight.ultsub.Utils;

import android.util.Log;

import com.nklight.ultsub.BuildConfig;

public class LogUtils {
    public static void d(String tag, String log) {
        if (BuildConfig.isLogEnable) {
            Log.d(tag, log);
        }
    }
}
