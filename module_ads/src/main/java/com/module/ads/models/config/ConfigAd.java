package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigAd {
    @SerializedName("ad_places_native")
    public List<ConfigAdPlaceNative> listAdPlaceNative;
    @SerializedName("config_screen_language")
    public ConfigScreenLanguage configScreenLanguage;
    @SerializedName("config_screen_onboard")
    public ConfigScreenOnboard configScreenOnboard;

    public List<ConfigAdPlaceNative> getListAdPlaceNative() {
        return listAdPlaceNative;
    }

    public ConfigScreenLanguage getConfigScreenLanguage() {
        return configScreenLanguage;
    }

    public ConfigScreenOnboard getConfigScreenOnboard() {
        return configScreenOnboard;
    }
}
