package com.module.ads.admob.inters;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
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


public class IntersSplashAll {

    private static IntersSplashAll intersSplashAll;
    private boolean isTimeOut = false;
    public static InterstitialAd interstitialAdHigh;
    public static InterstitialAd interstitialAdNormal;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;
    public boolean isShowing = false;

    public static IntersSplashAll getInstance() {
        if (intersSplashAll == null) {
            intersSplashAll = new IntersSplashAll();
        }
        return intersSplashAll;
    }


    public void loadAnsShow(final Activity activity, CallbackAd mCallBack) {
        isTimeOut = false;
        if (FirebaseQuery.getEnableIntersSplash2Floor()) {
            loadAdsHigh(activity, mCallBack);
        } else if (FirebaseQuery.getEnableIntersSplash()) {
            loadAdsNormal(activity, mCallBack);
        } else {
            mCallBack.onNextAction();
        }
        timeout(activity, mCallBack);
    }

    private void loadAdsHigh(Activity activity, CallbackAd mCallBack) {
        if (interstitialAdHigh != null || isLoadingHigh) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters()) {
            if (FirebaseQuery.getEnableIntersSplash2Floor()) {
                Log.e("TAG", "loadAdsHigh: inter splash high" );
                isLoadingHigh = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(activity, FirebaseQuery.getIdIntersSplash2Floor(), adRequest,
                        new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                Log.e("TAG", "onAdLoaded: inter splash high");
                                isLoadingHigh = false;
                                interstitialAdHigh = interstitialAd;
                                isTimeOut = true;
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
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        Log.e("TAG", "onAdFailedToShowFullScreenContent: inter splash high");
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        isShowing = false;
                                        isLoadingHigh = false;
                                        isLoadingNormal = false;
                                        interstitialAdHigh = null;
                                        interstitialAdNormal = null;
                                        IntersUtils.dismissDialogLoading();
                                        if (mCallBack != null) {
                                            mCallBack.onNextAction();
                                        }
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        isShowing = true;
                                        FBTracking.funcTracking(activity, "inter_splash_view", null);
                                    }
                                });

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        IntersUtils.showDialogLoading(activity);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (activity != null && !activity.isFinishing()) {
                                                    interstitialAd.show(activity);
                                                }
                                            }
                                        }, 500L);
                                    }
                                }, 3000L);
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.e("TAG", "onAdFailedToLoad: inter splash high");
                                isLoadingHigh = false;
                                interstitialAdHigh = null;
                                if (FirebaseQuery.getEnableIntersSplash()) {
                                    loadAdsNormal(activity, mCallBack);
                                } else {
                                    if (mCallBack != null) {
                                        mCallBack.onNextAction();
                                    }
                                }
                            }
                        });
            } else {
                isLoadingHigh = false;
                if (FirebaseQuery.getEnableIntersSplash()) {
                    loadAdsNormal(activity, mCallBack);
                }
            }
        } else {
            if (mCallBack != null) {
                mCallBack.onNextAction();
            }
        }
    }

    private void loadAdsNormal(Activity activity, CallbackAd mCallBack) {
        if (interstitialAdNormal != null || isLoadingNormal) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableInters() && FirebaseQuery.getEnableIntersSplash()) {
            Log.e("TAG", "loadAdsNormal: inter splash normal" );
            isLoadingNormal = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(activity, FirebaseQuery.getIdIntersSplash(), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            Log.e("TAG", "onAdLoaded: inter splash normal");
                            isTimeOut = true;
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
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    Log.e("TAG", "onAdFailedToShowFullScreenContent: inter splash normal");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    isShowing = false;
                                    isLoadingHigh = false;
                                    isLoadingNormal = false;
                                    interstitialAdHigh = null;
                                    interstitialAdNormal = null;
                                    IntersUtils.dismissDialogLoading();
                                    if (mCallBack != null) {
                                        mCallBack.onNextAction();
                                    }
                                }

                                @Override
                                public void onAdImpression() {
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    isShowing = true;
                                    FBTracking.funcTracking(activity, "inter_splash_view", null);
                                }
                            });
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    IntersUtils.showDialogLoading(activity);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (activity != null && !activity.isFinishing()) {
                                                interstitialAd.show(activity);
                                            }
                                        }
                                    }, 500L);
                                }
                            }, 3000L);

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: inter splash normal");
                            isTimeOut = true;
                            isLoadingHigh = false;
                            isLoadingNormal = false;
                            interstitialAdHigh = null;
                            interstitialAdNormal = null;
                            if (mCallBack != null) {
                                mCallBack.onNextAction();
                            }
                        }
                    });
        } else {
            if (mCallBack != null) {
                mCallBack.onNextAction();
            }
        }
    }

    private void timeout(Activity activity, CallbackAd callBack) {
        CountDownTimer countDownTimer = new CountDownTimer(30000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isTimeOut) {
                    if (activity != null && !activity.isFinishing()) {
                        if (interstitialAdHigh != null) {
                            interstitialAdHigh.show(activity);
                            cancel();
                            return;
                        }
                        if (interstitialAdNormal != null) {
                            interstitialAdNormal.show(activity);
                            cancel();
                            return;
                        }
                        if (callBack != null) {
                            callBack.onNextAction();
                            cancel();
                        }
                    }
                }
            }
        };
        countDownTimer.start();
    }

    public void showAdsAll(Activity activity) {
        try {
            if (activity != null && !activity.isFinishing()) {
                IntersUtils.dismissDialogLoading();
                InterstitialAd interstitialAd = null;
                if (interstitialAdHigh != null) {
                    interstitialAd = interstitialAdHigh;
                } else if (interstitialAdNormal != null) {
                    interstitialAd = interstitialAdNormal;
                }
                if (interstitialAd != null) {
                    interstitialAd.show(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
