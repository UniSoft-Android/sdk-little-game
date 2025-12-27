package com.module.ads.admob.banner;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.module.ads.callback.CallbackBanner;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;

public class BannerSplash {

    private static BannerSplash bannerSplash;
    private AdView mAdView;

    public static BannerSplash getInstance() {
        if (bannerSplash == null) {
            bannerSplash = new BannerSplash();
        }
        return bannerSplash;
    }

    public void loadAds(Activity activity, CallbackBanner callbackBanner) {
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableBanner()) {
            Log.e("TAG", "loadAds: banner splash");
            mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, BannerUtils.getAdWidth(activity)));

            mAdView.setOnPaidEventListener(new OnPaidEventListener() {
                @Override
                public void onPaidEvent(@NonNull AdValue adValue) {
                    double revenue = adValue.getValueMicros() / 1000000.0;
                    AdapterResponseInfo loadedAdapterResponseInfo = mAdView.getResponseInfo().getLoadedAdapterResponseInfo();
                    AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Banner");
                }
            });
            mAdView.setAdUnitId(FirebaseQuery.getIdBannerSplash());
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.e("TAG", "onAdLoaded: banner splash");
                    if (callbackBanner != null) {
                        callbackBanner.onLoaded();
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (callbackBanner != null) {
                        callbackBanner.onFailed();
                    }
                }

                @Override
                public void onAdImpression() {
                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                }
            });
            mAdView.loadAd(BannerUtils.getAdRequest(false));
        } else {
            if (callbackBanner != null) {
                callbackBanner.onFailed();
            }
        }
    }

    public void showAds(LinearLayout lnBanner) {
        try {
            Log.e("TAG", "showAds: banner splash");
            if (mAdView != null) {
                lnBanner.setVisibility(View.VISIBLE);
                lnBanner.removeAllViews();
                lnBanner.addView(mAdView);
            } else {
                lnBanner.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
