package com.module.ads.admob.natives;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.R;
import com.module.ads.callback.CallbackNative;
import com.module.ads.mmp.AdjustTracking;
import com.module.ads.remote.FirebaseQuery;
import com.module.ads.utils.FBTracking;
import com.module.ads.utils.PurchaseUtils;


public class NativeCollapse {

    private static NativeCollapse nativeCollapse;
    private static NativeAd nativeAd;

    public static NativeCollapse getInstance() {
        if (nativeCollapse == null) {
            nativeCollapse = new NativeCollapse();
        }
        return nativeCollapse;
    }

    private NativeCollapse() {
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, boolean isCollapse) {
        try {
            adView.setMediaView(adView.findViewById(R.id.ad_media));
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            if (adView.getHeadlineView() != null) {
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            }
            if (adView.getMediaView() != null) {
                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
            }

            if (isCollapse) {
                ImageView ivClose = adView.findViewById(R.id.iv_close_collapse);
                if (ivClose != null) {
                    if (adView.getMediaView() == null) {
                        adView.findViewById(R.id.cl_collapse).setVisibility(View.GONE);
                    } else {
                        adView.findViewById(R.id.cl_collapse).setVisibility(View.VISIBLE);
                    }
                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adView.findViewById(R.id.cl_collapse).setVisibility(View.GONE);
                        }
                    });
                } else {
                    if (adView.getMediaView() == null) {
                        adView.findViewById(R.id.cl_collapse).setVisibility(View.GONE);
                    } else {
                        adView.findViewById(R.id.cl_collapse).setVisibility(View.VISIBLE);
                    }
                }
            } else {
                adView.findViewById(R.id.cl_collapse).setVisibility(View.GONE);
            }

            if (adView.getBodyView() != null) {
                if (nativeAd.getBody() == null) {
                    adView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                }
            }

            if (adView.getCallToActionView() != null) {
                if (nativeAd.getCallToAction() == null) {
                    adView.getCallToActionView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getCallToActionView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
            }

            if (adView.getIconView() != null) {
                if (nativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }
            }
            if (adView.getPriceView() != null) {
                if (nativeAd.getPrice() == null) {
                    adView.getPriceView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
                }
            }

            if (adView.getStoreView() != null) {
                if (nativeAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
                }
            }
            if (adView.getStarRatingView() != null) {
                if (nativeAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {
                    ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }
            }

            if (adView.getAdvertiserView() != null) {
                if (nativeAd.getAdvertiser() == null) {
                    adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }
            }
            if (nativeAd.getMediaContent() != null) {
                VideoController vc = nativeAd.getMediaContent().getVideoController();
                if (vc.hasVideoContent()) {
                    vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                        @Override
                        public void onVideoEnd() {
                            super.onVideoEnd();
                        }
                    });
                }
            }

            adView.setNativeAd(nativeAd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadAndShow(final Activity activity, LinearLayout lnNative, String idAds, CallbackNative callbackNative) {
        try {
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && FirebaseQuery.getEnableNative()) {
                AdLoader.Builder builder = new AdLoader.Builder(activity, idAds);
                builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        NativeCollapse.nativeAd = nativeAd;
                        nativeAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                double revenue = adValue.getValueMicros() / 1000000.0;
                                AdapterResponseInfo loadedAdapterResponseInfo = nativeAd.getResponseInfo().getLoadedAdapterResponseInfo();
                                AdjustTracking.adjustTrackingRev(revenue, adValue.getCurrencyCode(), loadedAdapterResponseInfo.getAdSourceName());
                                FBTracking.funcTrackingIAA(activity, FBTracking.EVENT_AD_IMPRESSION, String.valueOf(revenue), activity.getClass(), "Native");
                            }
                        });
                        NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_collapse, null);
                        populateNativeAdView(nativeAd, nativeAdView, false);
                        lnNative.setVisibility(View.VISIBLE);
                        lnNative.removeAllViews();
                        lnNative.addView(nativeAdView);

                        if (callbackNative != null) {
                            callbackNative.onLoaded();
                        }
                    }
                });

                AdLoader adLoader = builder.withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
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

                VideoOptions videoOptions = new VideoOptions.Builder()
                        .build();

                NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
                builder.withNativeAdOptions(adOptions);
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                lnNative.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAds(Activity activity, LinearLayout lnNative, boolean isCollapse) {
        try {
            if (!PurchaseUtils.isNoAds(activity) && FirebaseQuery.getEnableAds() && nativeAd != null) {
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_collapse, null);
                populateNativeAdView(nativeAd, nativeAdView, isCollapse);
                lnNative.setVisibility(View.VISIBLE);
                lnNative.removeAllViews();
                lnNative.addView(nativeAdView);
            } else {
                lnNative.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
