package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

public class ConfigAdPlaceNative {
    @SerializedName("name")
    public String name;
    @SerializedName("button_cta")
    public ConfigButtonCta buttonCta;
    @SerializedName("background")
    public ConfigBackground background;
    @SerializedName("id_layout")
    public int idLayout;
    @SerializedName("content_text_color")
    public String contentTextColor;

    public String getName() {
        return name;
    }

    public ConfigButtonCta getButtonCta() {
        return buttonCta;
    }

    public ConfigBackground getBackground() {
        return background;
    }

    public int getIdLayout() {
        return idLayout;
    }

    public String getContentTextColor() {
        return contentTextColor;
    }
}
