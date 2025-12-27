package com.module.ads.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class EventTracking {
    private static final String PREF_NAME = "event_tracking_prefs";
    private static EventTracking instance;
    private final SharedPreferences prefs;

    private EventTracking(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Singleton init
    public static synchronized EventTracking getInstance(Context context) {
        if (instance == null) {
            instance = new EventTracking(context);
        }
        return instance;
    }

    // Track event chỉ 1 lần duy nhất
    public boolean trackingEvent(Context context, String eventName, Bundle bundle) {
        if (context == null) return false;
        boolean alreadyTracked = prefs.getBoolean(eventName, false);
        if (!alreadyTracked) {
            FBTracking.funcTracking(context, eventName, bundle);
            prefs.edit().putBoolean(eventName, true).apply(); // Đánh dấu đã track
            return true;
        }
        return false;
    }

    // Optional: Reset 1 event
    public void resetEvent(String eventName) {
        prefs.edit().remove(eventName).apply();
    }

    // Optional: Reset tất cả
    public void resetAll() {
        prefs.edit().clear().apply();
    }
}
