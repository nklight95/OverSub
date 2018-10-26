package com.nklight.ultsub.FloatBuble;

import com.nklight.ultsub.Utils.LogUtils;

public class FloatingBubbleLogger {
    private boolean isDebugEnabled;
    private String tag;

    public FloatingBubbleLogger() {
        isDebugEnabled = false;
        tag = FloatingBubbleLogger.class.getSimpleName();
    }

    public FloatingBubbleLogger setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public FloatingBubbleLogger setDebugEnabled(boolean enabled) {
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
