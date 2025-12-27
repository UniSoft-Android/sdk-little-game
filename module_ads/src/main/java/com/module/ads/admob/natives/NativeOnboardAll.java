package com.module.ads.admob.natives;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.callback.CallbackNative;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;


public class NativeOnboardAll {

    private static NativeOnboardAll nativeOnBoard;
    public static NativeAd nativeAdHigh;
    public static NativeAd nativeAdNormal;
    public static NativeAd nativeAdAll;
    private boolean isLoadingHigh = false;
    private boolean isLoadingNormal = false;
    private boolean isLoadingAll = false;

    public static NativeOnboardAll getInstance() {
        if (nativeOnBoard == null) {
            nativeOnBoard = new NativeOnboardAll();
        }
        return nativeOnBoard;
    }

    private CallbackNative callbackNative;

    public void setCallbackNative(CallbackNative callbackNative) {
        this.callbackNative = callbackNative;
    }

    public void loadAndShow(final Activity activity, LinearLayout lnNative, String id, CallbackNative callbackNative, String namePlace) {
        try {
            if (isLoadingAll) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                isLoadingAll = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, id);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        isLoadingAll = false;
                        nativeAdAll = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                long revenue = adValue.getValueMicros() / 1000000;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                            }
                        });
                        NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNative(namePlace), null);
                        NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                        lnNative.setVisibility(View.VISIBLE);
                        lnNative.removeAllViews();
                        lnNative.addView(nativeAdView);
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isLoadingAll = false;
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAll = false;
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }).build();

                VideoOptions videoOptions = new VideoOptions.Builder().build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                lnNative.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            isLoadingAll = false;
            e.printStackTrace();
        }
    }

    public void loadAndShowAll(Activity activity, LinearLayout lnNative, CallbackNative callbackNative, String namePlace) {
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
            if (FirebaseQuery.getEnableNativeOnboard2Floor()) {
                loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeOnboarding2Floor(), new CallbackNative() {
                    @Override
                    public void onLoaded() {
                        if (nativeAdAll != null) {
                            NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                loadAndShowAll(activity, lnNative, null, namePlace);
                            });
                        }
                    }

                    @Override
                    public void onFailed() {
                        if (FirebaseQuery.getEnableNativeOnboard()) {
                            loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeOnboarding(), new CallbackNative() {
                                @Override
                                public void onLoaded() {
                                    if (nativeAdAll != null) {
                                        NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                            loadAndShowAll(activity, lnNative, null, namePlace);
                                        });
                                    }
                                }

                                @Override
                                public void onFailed() {
                                }

                                @Override
                                public void onAdImpression() {
                                }
                            }, namePlace);
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }, namePlace);
            } else {
                if (FirebaseQuery.getEnableNativeOnboard()) {
                    loadAndShow(activity, lnNative, FirebaseQuery.getIdNativeOnboarding(), new CallbackNative() {
                        @Override
                        public void onLoaded() {
                            if (nativeAdAll != null) {
                                NativeUtils.handleNativeMedia(nativeAdAll, namePlace, () -> {
                                    loadAndShowAll(activity, lnNative, null, namePlace);
                                });
                            }
                        }

                        @Override
                        public void onFailed() {
                        }

                        @Override
                        public void onAdImpression() {
                        }
                    }, namePlace);
                }
            }
        }
    }

    private void loadAdsHigh(final Activity activity) {
        try {
            if (isLoadingHigh || nativeAdHigh != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                if (FirebaseQuery.getEnableNativeOnboard2Floor()) {
                    isLoadingHigh = true;
                    AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeOnboarding2Floor());
                    builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            isLoadingHigh = false;
                            NativeOnboardAll.nativeAdHigh = nativeAd;
                            nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    double revenue = adValue.getValueMicros() / 1000000.0;
                                    AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                    AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                                }
                            });
                        }
                    });

                    AdLoader adLoader = builder.withAdListener(new AdListener() {

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            Log.e("TAG", "onAdLoaded: native onboard high");
                            isLoadingHigh = false;
                            if (callbackNative != null) {
                                callbackNative.onLoaded();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: native onboard high");
                            isLoadingHigh = false;
                            if (FirebaseQuery.getEnableNativeOnboard()) {
                                loadAdsNormal(activity);
                            } else {
                                if (callbackNative != null) {
                                    callbackNative.onFailed();
                                }
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                            if (callbackNative != null) {
                                callbackNative.onAdImpression();
                            }
                        }
                    }).build();

                    VideoOptions videoOptions = new VideoOptions.Builder().build();
                    NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                    builder.withNativeAdOptions(adOptions);
                    adLoader.loadAd(new AdRequest.Builder().build());
                } else {
                    isLoadingHigh = false;
                    if (FirebaseQuery.getEnableNativeOnboard()) {
                        loadAdsNormal(activity);
                    } else {
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
                    }
                }
            } else {
                isLoadingHigh = false;
                if (callbackNative != null) {
                    callbackNative.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingHigh = false;
            e.printStackTrace();
        }
    }

    private void loadAdsNormal(final Activity activity) {
        try {
            if (isLoadingNormal || nativeAdNormal != null) return;
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative() && FirebaseQuery.getEnableNativeOnboard()) {
                isLoadingNormal = true;
                AdLoader.Builder builder = new AdLoader.Builder(activity, FirebaseQuery.getIdNativeOnboarding());
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        isLoadingNormal = false;
                        nativeAdNormal = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                double revenue = adValue.getValueMicros() / 1000000.0;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                            }
                        });
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.e("TAG", "onAdLoaded: native onboard normal");
                        isLoadingNormal = false;
                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e("TAG", "onAdFailedToLoad: native onboard normal");
                        isLoadingNormal = false;
                        if (callbackNative != null) {
                            callbackNative.onFailed();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                        if (callbackNative != null) {
                            callbackNative.onAdImpression();
                        }
                    }
                }).build();

                VideoOptions videoOptions = new VideoOptions.Builder().build();
                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                isLoadingNormal = false;
                if (callbackNative != null) {
                    callbackNative.onFailed();
                }
            }
        } catch (Exception e) {
            isLoadingNormal = false;
            e.printStackTrace();
        }
    }

    public void loadAdsAll(Activity activity) {
        if (FirebaseQuery.getEnableNativeOnboard2Floor()) {
            loadAdsHigh(activity);
        } else if (FirebaseQuery.getEnableNativeOnboard()) {
            loadAdsNormal(activity);
        } else {
            if (callbackNative != null) {
                callbackNative.onFailed();
            }
        }
    }

    public void showAdsAll(Activity activity, LinearLayout lnNative, String namePlace) {
        try {
            NativeAd nativeAd = null;
            if (nativeAdHigh != null) {
                nativeAd = nativeAdHigh;
            } else if (nativeAdNormal != null) {
                nativeAd = nativeAdNormal;
            }
            if (nativeAd != null) {
                lnNative.setVisibility(View.VISIBLE);
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(NativeUtils.getLayoutNative(namePlace), null);
                NativeUtils.populateNativeAdView(nativeAd, nativeAdView, namePlace);
                lnNative.removeAllViews();
                lnNative.addView(nativeAdView);

                NativeUtils.handleNativeMedia(nativeAd, namePlace, () -> {
                    loadAndShowAll(activity, lnNative, null, namePlace);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyNativeAd() {
        nativeAdHigh = null;
        nativeAdNormal = null;
        callbackNative = null;
    }

    public void destroyNativeAdAll() {
        nativeAdHigh = null;
        nativeAdNormal = null;
        nativeAdAll = null;
        callbackNative = null;
    }
}
