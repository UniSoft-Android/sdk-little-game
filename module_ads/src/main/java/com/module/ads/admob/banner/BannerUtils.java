package com.module.ads.admob.banner;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

public class BannerUtils {

    public static AdRequest getAdRequest(boolean isCollapsible) {
        AdRequest.Builder builder = new AdRequest.Builder();
        Bundle extras = new Bundle();
        if (isCollapsible) {
            extras.putString("collapsible", "bottom");
        }
        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        return builder.build();
    }

    public static int getAdWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        return (int) (adWidthPixels / density);
    }
}
