package com.module.ads.admob.natives;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.module.ads.R;
import com.module.ads.models.config.ConfigAdPlaceNative;
import com.module.ads.models.config.ConfigBackground;
import com.module.ads.models.config.ConfigButtonCta;
import com.module.ads.remote.FirebaseQuery;

import java.util.HashMap;
import java.util.Map;

public class NativeUtils {
    private static final Map<String, CountDownTimer> timers = new HashMap<>();
    private static final Map<String, Boolean> destroyedMap = new HashMap<>();

    public static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, String namePlace) {
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

            try {
                RelativeLayout rlRoot = adView.findViewById(R.id.rl_root);
                if (FirebaseQuery.getConfigController().configAd != null && rlRoot != null) {
                    ConfigAdPlaceNative configAdPlaceNative = FirebaseQuery.getConfigController().configMap.get(namePlace);
                    if (configAdPlaceNative != null) {
                        ConfigBackground bgObj = configAdPlaceNative.getBackground();
                        if (bgObj != null) {
                            // Lấy Drawable gốc từ background và clone nó (mutate)
                            Drawable bg = rlRoot.getBackground();
                            if (bg instanceof GradientDrawable) {
                                GradientDrawable drawable = (GradientDrawable) bg.mutate();

                                if (bgObj.isStroke()) {
                                    // Đặt màu nền và viền
                                    drawable.setColor(Color.parseColor(bgObj.getColor()));
                                    drawable.setStroke(bgObj.getStrokeSize(), Color.parseColor(bgObj.getStrokeColor()));
                                } else {
                                    // Chỉ đổi màu nền, bỏ viền nếu có
                                    drawable.setColor(Color.parseColor(bgObj.getColor()));
                                    drawable.setStroke(0, Color.TRANSPARENT);
                                }
                                drawable.setCornerRadius(bgObj.getCornerRadius());
                                rlRoot.setBackground(drawable);
                            } else {
                                GradientDrawable fallback = new GradientDrawable();
                                fallback.setColor(Color.parseColor(bgObj.getColor()));
                                if (bgObj.isStroke()) {
                                    fallback.setStroke(bgObj.getStrokeSize(), Color.parseColor(bgObj.getStrokeColor()));
                                }
                                fallback.setCornerRadius(bgObj.getCornerRadius());
                                rlRoot.setBackground(fallback);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (adView.getHeadlineView() != null) {
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
                setContentTextColor(((TextView) adView.getHeadlineView()), namePlace);
            }
            if (adView.getMediaView() != null) {
                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
            }

            if (adView.getBodyView() != null) {
                if (nativeAd.getBody() == null) {
                    adView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                    setContentTextColor(((TextView) adView.getBodyView()), namePlace);
                }
            }

            if (adView.getCallToActionView() != null) {
                if (nativeAd.getCallToAction() == null) {
                    adView.getCallToActionView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getCallToActionView().setVisibility(View.VISIBLE);
                    TextView tvCallToAction = (TextView) adView.getCallToActionView();
                    tvCallToAction.setText(nativeAd.getCallToAction());
                    if (FirebaseQuery.getConfigController().configAd != null) {
                        ConfigAdPlaceNative configAdPlaceNative = FirebaseQuery.getConfigController().configMap.get(namePlace);
                        if (configAdPlaceNative != null) {
                            ConfigButtonCta configButtonCta = configAdPlaceNative.buttonCta;
                            String startColor = configButtonCta.getStartColor();
                            String endColor = configButtonCta.getEndColor();
                            int[] colors = {
                                    Color.parseColor(startColor), // start color
                                    Color.parseColor(endColor)  // end color
                            };

                            GradientDrawable.Orientation orientation;
                            if (configButtonCta.isColorLeftToRight()) {
                                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                            } else {
                                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                            }

                            GradientDrawable gradient = new GradientDrawable(orientation, colors);
                            gradient.setCornerRadius((float) configButtonCta.getCornerRadius());
                            if (configButtonCta.isStroke()) {
                                gradient.setStroke(configButtonCta.getStrokeSize(), Color.parseColor(configButtonCta.getStrokeColor()));
                            }
                            tvCallToAction.setBackground(gradient);
                            tvCallToAction.setTextColor(Color.parseColor(configButtonCta.getTextColor()));
                        }
                    }
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
                    setContentTextColor((TextView) adView.getPriceView(), namePlace);
                }
            }

            if (adView.getStoreView() != null) {
                if (nativeAd.getStore() == null) {
                    adView.getStoreView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
                    setContentTextColor((TextView) adView.getStoreView(), namePlace);
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
                    setContentTextColor((TextView) adView.getAdvertiserView(), namePlace);
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

    private static void setContentTextColor(TextView textContent, String namePlace) {
        try {
            ConfigAdPlaceNative configAdPlaceNative = FirebaseQuery.getConfigController().configMap.get(namePlace);
            if (configAdPlaceNative != null) {
                String color = configAdPlaceNative.getContentTextColor();
                textContent.setTextColor(Color.parseColor(color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getLayoutNative(String namePlace) {
        try {
            ConfigAdPlaceNative adPlaceNative = FirebaseQuery.getConfigController().configMap.get(namePlace);
            int idLayout = R.layout.layout_native_2;
            if (adPlaceNative != null) {
                switch (adPlaceNative.idLayout) {
                    case 1:
                        idLayout = R.layout.layout_native_1;
                        break;
                    case 2:
                        idLayout = R.layout.layout_native_2;
                        break;
                    case 3:
                        idLayout = R.layout.layout_native_3;
                        break;
                    case 4:
                        idLayout = R.layout.layout_native_4;
                        break;
                    case 5:
                        idLayout = R.layout.layout_native_5;
                        break;
                    case 6:
                        idLayout = R.layout.layout_native_6;
                        break;
                    case 7:
                        idLayout = R.layout.layout_native_7;
                        break;
                    case 8:
                        idLayout = R.layout.layout_native_8;
                        break;
                    case 9:
                        idLayout = R.layout.layout_native_9;
                        break;
                    case 10:
                        idLayout = R.layout.layout_native_10;
                        break;
                    case 11:
                        idLayout = R.layout.layout_native_11;
                        break;
                    case 12:
                        idLayout = R.layout.layout_native_12;
                        break;
                    case 13:
                        idLayout = R.layout.layout_native_13;
                        break;
                }
            }
            return idLayout;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.layout.layout_native_2;
    }

    public static int getLayoutNativeFull(String namePlace) {
        try {
            ConfigAdPlaceNative adPlaceNative = FirebaseQuery.getConfigController().configMap.get(namePlace);
            int idLayout = R.layout.layout_native_fullscreen_1;
            if (adPlaceNative != null) {
                switch (adPlaceNative.idLayout) {
                    case 1:
                        idLayout = R.layout.layout_native_fullscreen_1;
                        break;
                    case 2:
                        idLayout = R.layout.layout_native_fullscreen_2;
                        break;
                    case 3:
                        idLayout = R.layout.layout_native_fullscreen_3;
                        break;
                    case 4:
                        idLayout = R.layout.layout_native_fullscreen_4;
                        break;
                }
            }
            return idLayout;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.layout.layout_native_fullscreen_1;
    }

    public static void startImageTimer(String namePlace, Runnable onFinish) {
        cancelTimer(namePlace);

        CountDownTimer timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onFinish.run();
            }
        };

        timers.put(namePlace, timer);
        timer.start();
    }

    public static void cancelTimer(String namePlace) {
        CountDownTimer timer = timers.get(namePlace);
        if (timer != null) {
            timer.cancel();
        }
        timers.remove(namePlace);
    }

    public static void destroyFlag(String namePlace) {
        destroyedMap.put(namePlace, true);
    }

    public static void resetDestroyFlag(String namePlace) {
        destroyedMap.put(namePlace, false);
    }

    public static void handleNativeMedia(NativeAd nativeAd, String namePlace, Runnable reloadCallback) {
        Log.e("TAG", "handleNativeMedia: " + namePlace + " - " +
                !FirebaseQuery.getEnableNativeReloadMedia() + " - " +
                (nativeAd.getMediaContent() == null)
        );
        Boolean state = destroyedMap.get(namePlace);
        if (Boolean.TRUE.equals(state)) {
            Log.e("TAG", "handleNativeMedia: Bị chặn vì màn hình đã destroy");
            return;
        }
        if (!FirebaseQuery.getEnableNativeReloadMedia()) return;
        if (nativeAd.getMediaContent() == null) return;
        MediaContent mediaContent = nativeAd.getMediaContent();
        // Nếu là VIDEO → reload khi video kết thúc
        if (mediaContent.hasVideoContent()) {
            Log.e("TAG", "handleNativeMedia: hasVideoContent");
            VideoController vc = mediaContent.getVideoController();

            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                    // Hủy timer ảnh nếu có
                    cancelTimer(namePlace);

                    reloadCallback.run();
                }
            });

        } else {
            Log.e("TAG", "handleNativeMedia: hasMediaContent");
            // Nếu là ẢNH → reload sau 15s
            startImageTimer(namePlace, reloadCallback);
        }
    }
}
