package com.module.ads.admob.inters;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.module.ads.callback.CallbackAd;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;


public class IntersInApp {

    public CallbackAd mCallbackAd;
    public static InterstitialAd interstitialAdHigh;
    public static InterstitialAd interstitialAdNormal;
    public boolean isShowing = false;
    private long timeLoad = 0;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;

    private static IntersInApp mIntersInApp;

    public static IntersInApp getInstance() {
        if (mIntersInApp == null) {
            mIntersInApp = new IntersInApp();
        }
        return mIntersInApp;
    }

    private void loadAdsHigh(final Activity activity) {
        if (interstitialAdHigh != null || isLoadingHigh) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters()) {
            if (FirebaseQuery.getEnableIntersInApp2Floor()) {
                isLoadingHigh = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, FirebaseQuery.getIdIntersInApp2Floor(), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                Log.e("TAG", "onAdLoaded: inter all high");
                                isLoadingHigh = false;
                                interstitialAdHigh = interstitialAd;
                                interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                                    @Override
                                    public void onPaidEvent(@NonNull AdValue adValue) {
                                        double revenue = adValue.getValueMicros() / 1000000.0;
                                        AdapterResponseInfo loadedAdapterResponseInfo = interstitialAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                        AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Interstitial");
                                    }
                                });
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        isShowing = false;
                                        isLoadingHigh = false;
                                        isLoadingNormal = false;
                                        interstitialAdHigh = null;
                                        loadAdsAll(activity);
                                        timeLoad = System.currentTimeMillis();
                                        IntersUtils.dismissDialogLoading();
                                        if (mCallbackAd != null) {
                                            mCallbackAd.onNextAction();
                                        }
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent();
                                        isShowing = true;
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.e("TAG", "onAdFailedToLoad: inter all high");
                                isLoadingHigh = false;
                                interstitialAdHigh = null;
                                if (FirebaseQuery.getEnableIntersInApp()) {
                                    loadAdsNormal(activity);
                                }
                            }
                        });
            } else {
                isLoadingHigh = false;
                if (FirebaseQuery.getEnableIntersInApp()) {
                    loadAdsNormal(activity);
                }
            }
        }
    }

    private void loadAdsNormal(final Activity activity) {
        if (interstitialAdNormal != null || isLoadingNormal) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters() && FirebaseQuery.getEnableIntersInApp()) {
            isLoadingNormal = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, FirebaseQuery.getIdIntersInApp(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.e("TAG", "onAdLoaded: inter all normal");
                            isLoadingNormal = false;
                            interstitialAdNormal = interstitialAd;
                            interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    double revenue = adValue.getValueMicros() / 1000000.0;
                                    AdapterResponseInfo loadedAdapterResponseInfo = interstitialAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                    AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Interstitial");
                                }
                            });
                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    isLoadingHigh = false;
                                    isLoadingNormal = false;
                                    interstitialAdHigh = null;
                                    interstitialAdNormal = null;
                                    loadAdsAll(activity);
                                    timeLoad = System.currentTimeMillis();
                                    IntersUtils.dismissDialogLoading();
                                    if (mCallbackAd != null) {
                                        mCallbackAd.onNextAction();
                                    }
                                }

                                @Override
                                public void onAdImpression() {
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent();
                                    isShowing = true;
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: inter all normal");
                            isLoadingHigh = false;
                            isLoadingNormal = false;
                            interstitialAdHigh = null;
                            interstitialAdNormal = null;
                        }
                    });

        }
    }

    public void loadAdsAll(Activity activity) {
        if (FirebaseQuery.getEnableIntersInApp2Floor()) {
            loadAdsHigh(activity);
        } else if (FirebaseQuery.getEnableIntersInApp()) {
            loadAdsNormal(activity);
        }
    }

    public void showInters(Activity activity, CallbackAd callbackAd) {
        this.mCallbackAd = callbackAd;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters()) {
            InterstitialAd interstitialAd;
            if (interstitialAdHigh != null) {
                interstitialAd = interstitialAdHigh;
            } else if (interstitialAdNormal != null) {
                interstitialAd = interstitialAdNormal;
            } else {
                interstitialAd = null;
            }
            if (interstitialAd != null) {
                long timeQuery = Long.parseLong(FirebaseQuery.getTimeShowInters());
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - timeLoad;
                if (elapsedTime >= timeQuery) {
                    IntersUtils.showDialogLoading(activity);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (interstitialAd != null) {
                                interstitialAd.show(activity);
                            }
                        }
                    }, 500L);
                } else {
                    if (mCallbackAd != null) {
                        mCallbackAd.onNextAction();
                    }
                }
            } else {
                loadAdsAll(activity);
                if (callbackAd != null) {
                    callbackAd.onNextAction();
                }
            }
        } else {
            if (callbackAd != null) {
                callbackAd.onNextAction();
            }
        }
    }
}
