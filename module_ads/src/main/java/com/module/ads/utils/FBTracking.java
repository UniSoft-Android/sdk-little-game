package com.module.ads.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FBTracking {

    public static final String EVENT_AD_IMPRESSION = "ad_impression_uni";

    public static void funcTrackingAction(Context context, String nameEvent, String value) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("action_click", value);
        mFirebaseAnalytics.logEvent(nameEvent, bundle);
    }

    public static void funcTrackingIAA(Context context, String nameEvent, String value, Class myClass, String nameFormat) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("action_click", value);
        bundle.putString("name_screen", myClass.getName());
        bundle.putString("name_format", nameFormat);
        mFirebaseAnalytics.logEvent(nameEvent, bundle);
    }

    public static void funcTrackingIAA(Context context, String nameEvent) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(nameEvent, null);
    }

    public void setUserProperty(Context context, String propertyName, String value) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserProperty(propertyName, value);
    }

    public static void funcTracking(Context context, String nameEvent, Bundle bundle) {
        Log.e("tracking", "logEvent: " + nameEvent );
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(nameEvent, bundle);
    }
}
