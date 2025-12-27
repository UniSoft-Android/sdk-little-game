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


public class IntersOnboardAll {

    public CallbackAd mCallbackAd;
    private InterstitialAd interstitialAdHigh;
    private InterstitialAd interstitialAdNormal;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;
    private long timeLoad = 0;
    public boolean isShowing = false;
    private static IntersOnboardAll intersOnBoarding;

    public static IntersOnboardAll getInstance() {
        if (intersOnBoarding == null) {
            intersOnBoarding = new IntersOnboardAll();
        }
        return intersOnBoarding;
    }

    private void loadAdsHigh(final Activity activity) {
        if (interstitialAdHigh != null || isLoadingHigh) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters()) {
            if (FirebaseQuery.getEnableIntersOnboard2Floor()) {
                isLoadingHigh = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, FirebaseQuery.getIdIntersOnboard2floor(), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                Log.e("TAG", "onAdLoaded: inter onboard high");
                                isLoadingHigh = false;
                                IntersOnboardAll.this.interstitialAdHigh = interstitialAd;
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
                                        interstitialAdNormal = null;
                                        timeLoad = System.currentTimeMillis();
                                        IntersUtils.dismissDialogLoading();
                                        if (mCallbackAd != null) {
                                            mCallbackAd.onNextAction();
                                        }
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        isShowing = true;
                                        FBTracking.funcTracking(activity, "inter_onboarding_view", null);
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.e("TAG", "onAdFailedToLoad: inter onboard high " + loadAdError.getMessage());
                                isLoadingHigh = false;
                                interstitialAdHigh = null;
                                if (FirebaseQuery.getEnableIntersOnboard()) {
                                    loadAdsNormal(activity);
                                }
                            }
                        });
            } else {
                isLoadingHigh = false;
                if (FirebaseQuery.getEnableIntersOnboard()) {
                    loadAdsNormal(activity);
                }
            }
        }
    }

    private void loadAdsNormal(final Activity activity) {
        if (interstitialAdNormal != null || isLoadingNormal) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters() && FirebaseQuery.getEnableIntersOnboard()) {
            isLoadingNormal = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, FirebaseQuery.getIdIntersOnboard(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.e("TAG", "onAdLoaded: inter onboard normal");
                            isLoadingNormal = false;
                            IntersOnboardAll.this.interstitialAdNormal = interstitialAd;
                            interstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    long revenue = adValue.getValueMicros() / 1000000;
                                    AdapterResponseInfo loadedAdapterResponseInfo = interstitialAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                    AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
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
                                    interstitialAdNormal = null;
                                    timeLoad = System.currentTimeMillis();
                                    IntersUtils.dismissDialogLoading();
                                    if (mCallbackAd != null) {
                                        mCallbackAd.onNextAction();
                                    }
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    isShowing = true;
                                    FBTracking.funcTracking(activity, "inter_onboarding_view", null);
                                }

                                @Override
                                public void onAdImpression() {
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: inter onboard normal");
                            isLoadingHigh = false;
                            isLoadingNormal = false;
                            interstitialAdHigh = null;
                            interstitialAdNormal = null;
                        }
                    });
        }
    }

    public void loadAdsAll(Activity activity) {
        if (FirebaseQuery.getEnableIntersOnboard2Floor()) {
            loadAdsHigh(activity);
        } else if (FirebaseQuery.getEnableIntersOnboard()) {
            loadAdsNormal(activity);
        }
    }

    public void showIntersInScreen(Activity activity, CallbackAd callbackAd) {
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

                IntersUtils.showDialogLoading(activity);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitialAd != null){
                            interstitialAd.show(activity);
                        }
                    }
                }, 500L);
            } else {
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
