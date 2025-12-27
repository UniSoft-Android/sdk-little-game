package com.module.ads.admob.aoa;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.module.ads.callback.CallbackAd;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.HomeUtils;
import com.module.ads.utils.PurchaseUtils;
import com.module.ads.views.LoadingAdDialog;

import java.util.Date;

public class OpenAdsManager {
    private final AppOpenAd mAppOpenAd = null;
    public long loadTime;
    private CallbackAd mCallBack;
    private LoadingAdDialog loadingAdDialog;
    private boolean isShowing = false;
    private boolean isTimeOut = false;
    private static OpenAdsManager openAds;
    public boolean shouldReloadAd = false;
    public static AppOpenAd appOpenAd;

    public static OpenAdsManager getOpenAds() {
        if (openAds == null) {
            openAds = new OpenAdsManager();
        }
        return openAds;
    }

    public void loadAndShow(final Activity activity, CallbackAd callbackAd) {
        mCallBack = callbackAd;
        if (!isAdAvailable()) {
            isTimeOut = false;
            HomeUtils.setHomeClick(false);
            AppOpenAd.AppOpenAdLoadCallback mAppOpenAdLoadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd appOpenAd) {
                    OpenAdsManager.appOpenAd = appOpenAd;
                    isTimeOut = true;
                    isShowing = true;
                    appOpenAd.setOnPaidEventListener(new OnPaidEventListener() {
                        @Override
                        public void onPaidEvent(@NonNull AdValue adValue) {
                            long revenue = adValue.getValueMicros() / 1000000;
                            AdapterResponseInfo loadedAdapterResponseInfo = appOpenAd.getResponseInfo().getLoadedAdapterResponseInfo();
                            AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                            FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "OpenAds");
                        }
                    });
                    appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            isShowing = false;
                            try {
                                if (loadingAdDialog != null) {
                                    loadingAdDialog.dismiss();
                                    loadingAdDialog = null;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (mCallBack != null) {
                                mCallBack.onNextAction();
                            }
                        }

                        @Override
                        public void onAdImpression() {
                            FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                            isTimeOut = true;
                        }
                    });
                    try {
                        if (loadingAdDialog == null) {
                            loadingAdDialog = new LoadingAdDialog(activity);
                            loadingAdDialog.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            appOpenAd.show(activity);
                        }
                    }, 500L);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    isShowing = true;
                    isTimeOut = true;
                }
            };
            AdRequest adRequest = new AdRequest.Builder().build();
            AppOpenAd.load(activity, FirebaseQuery.getIdOpenStart(), adRequest, mAppOpenAdLoadCallback);
            timeout(callbackAd);
        } else {
            if (mCallBack != null) {
                mCallBack.onNextAction();
            }
        }
    }

    private void timeout(CallbackAd callBack) {
        CountDownTimer countDownTimer = new CountDownTimer(30000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isTimeOut) {
                    if (callBack != null) {
                        callBack.onNextAction();
                        cancel();
                    }
                }
            }
        };
        countDownTimer.start();
    }

    private boolean wasLoadTimeLessThanNHoursAgo() {
        return new Date().getTime() - loadTime < 14400000;
    }

    private boolean isAdAvailable() {
        return mAppOpenAd != null && wasLoadTimeLessThanNHoursAgo();
    }

    public void showOpenAds(Activity activity, CallbackAd callback) {
        HomeUtils.setHomeClick(false);
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableOpenStart() && FirebaseQuery.getEnableAds()) {
            if (!isShowing) {
                loadAndShow(activity, callback);
                isShowing = true;
            } else {
                if (callback != null) {
                    callback.onNextAction();
                }
            }
        } else {
            if (callback != null) {
                callback.onNextAction();
            }
        }
    }

    public void showAdsOpenStart(Activity activity) {
        try {
            if (loadingAdDialog != null) {
                loadingAdDialog.dismiss();
                loadingAdDialog = null;
            }
            if (activity != null && !activity.isFinishing() && appOpenAd != null) {
                appOpenAd.show(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
