package com.nklight.ultsub.FloatBuble;

import com.nklight.ultsub.Utils.LogUtils;

public class FloatBubbleLogger {
    private boolean isDebugEnabled;
    private String tag;

    public FloatBubbleLogger() {
        isDebugEnabled = false;
        tag = FloatBubbleLogger.class.getSimpleName();
    }

    public FloatBubbleLogger setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public FloatBubbleLogger setDebugEnabled(boolean enabled) {
        this.isDebugEnabled = enabled;
        return this;
    }

    public void log(String message) {
        if (isDebugEnabled) {
            LogUtils.d(tag, message);
        }
    }

    public void log(String message, Throwable throwable) {
        if (isDebugEnabled) {
            LogUtils.e(tag, message, throwable);
        }
    }
}
