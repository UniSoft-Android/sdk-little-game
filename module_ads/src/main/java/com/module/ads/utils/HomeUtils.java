package com.module.ads.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;

public class HomeUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static IntentFilter mFilter;
    private static ResumeReceiver mReceiver;
    public OnHomeResumeListener mListener;

    public static final String HOME_CLICK = "home_click";

    class ResumeReceiver extends BroadcastReceiver {

        ResumeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null && intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String stringExtra = intent.getStringExtra("reason");
                if (stringExtra != null && mListener != null) {
                    if (stringExtra.equals("homekey")) {
                        HomeUtils.this.mListener.onHomePressed();
                    } else if (stringExtra.equals("recentapps")) {
                        HomeUtils.this.mListener.onHomeLongPressed();
                    }
                }
            }
        }
    }

    public interface OnHomeResumeListener {
        void onHomeLongPressed();

        void onHomePressed();
    }

    public HomeUtils(Context context) {
        mContext = context;
        mFilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
    }

    public void setOnHomePressedListener(OnHomeResumeListener onHomeResumeListener) {
        this.mListener = onHomeResumeListener;
        mReceiver = new ResumeReceiver();
    }

    public static void startHome() {
        if (mReceiver != null) {
            ContextCompat.registerReceiver(mContext, mReceiver, mFilter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    public static void stopHome() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public static void setHomeClick(boolean isHomeClick) {
        SharePreferUtils.putBoolean(HOME_CLICK, isHomeClick);
    }

    public static boolean getHomeClick() {
        return SharePreferUtils.getBoolean(HOME_CLICK, false);
    }
}
