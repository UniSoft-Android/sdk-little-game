package com.module.ads.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.module.ads.BuildConfig;


public class PurchaseUtils {

    public static final String LICENSE_KEY = "";
    private static final String PURCHASE_UTILS = "PURCHASE_UTILS";
    private static final String ID_REMOVE_ADS = "ID_REMOVE_ADS";
    private static final String ID_NO_SUB = "ID_NO_SUB";

    public static String getIdRemoveAds() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "remove_ads"; // Test "android.test.purchased"
        }
    }

    public static String getIdWeek() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "sub_week"; // Test "android.test.purchased"
        }
    }

    public static String getIdMonth() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "sub_month"; // Test "android.test.purchased"
        }
    }

    public static String getIdTrialYear() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "sub_trial_3day_year"; // Test "android.test.purchased"
        }
    }

    public static String getIdYear() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "sub_year"; // Test "android.test.purchased"
        }
    }

    public static String getIdOneTime() {
        if (BuildConfig.DEBUG) {
            return "android.test.purchased";
        } else {
            return "purchase_one_time"; // Test "android.test.purchased"
        }
    }

    public static boolean isNoAds(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        return preferences.getBoolean(ID_REMOVE_ADS, false);
    }

    public static void setNoAds(Context mContext, boolean isPurchase) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ID_REMOVE_ADS, isPurchase);
        editor.apply();
    }

    public static boolean isNoSub(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        return preferences.getBoolean(ID_NO_SUB, false);
    }

    public static void setNoSub(Context mContext, boolean isPurchase) {
        SharedPreferences preferences = mContext.getSharedPreferences(PURCHASE_UTILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ID_NO_SUB, isPurchase);
        editor.apply();
    }
}
