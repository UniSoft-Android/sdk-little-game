package com.module.ads.admob.reward;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.module.ads.callback.CallbackAd;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;

public class RewardInApp {

    private static RewardInApp rewardInApp;
    private RewardedAd rewardedAd;
    private CallbackAd myCallback;
    public boolean isShowing = false;
    private boolean isLoadingAd = false;

    public static RewardInApp getInstance() {
        if (rewardInApp == null) {
            rewardInApp = new RewardInApp();
        }
        return rewardInApp;
    }

    private RewardInApp() {
    }

    public void loadReward(Activity activity) {
        if (isLoadingAd) return;
        if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableReward()) {
            isLoadingAd = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(activity, FirebaseQuery.getIdRewardAll(),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.e("TAG", "onAdFailedToLoad: " + loadAdError.getMessage());
                            isLoadingAd = false;
                            rewardedAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd ad) {
                            Log.e("TAG", "onAdLoaded: reward in app");
                            isLoadingAd = false;
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    long revenue = adValue.getValueMicros() / 1000000;
                                    AdapterResponseInfo loadedAdapterResponseInfo = ad.getResponseInfo().getLoadedAdapterResponseInfo();
                                    AdjustTracking.adjustTrackingRev(adValue.getValueMicros() / 1000000.0, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Reward");
                                }
                            });
                            rewardedAd = ad;
                            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    rewardedAd = null;
                                    isShowing = false;
                                    isLoadingAd = false;
                                    loadReward(activity);
                                    if (myCallback != null) {
                                        myCallback.onNextAction();
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                }

                                @Override
                                public void onAdImpression() {
                                    FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    isShowing = true;
                                }
                            });
                        }
                    });
        }
    }

    public void showReward(Activity activity, CallbackAd callback) {
        if (!PurchaseUtils.isNoAds(activity)) {
            if (rewardedAd != null) {
                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        myCallback = callback;
                    }
                });
            } else {
                Toast.makeText(activity, "Reward Ad not available!", Toast.LENGTH_SHORT).show();
                loadReward(activity);
            }
        } else {
            if (callback != null) {
                callback.onNextAction();
            }
        }
    }
}
