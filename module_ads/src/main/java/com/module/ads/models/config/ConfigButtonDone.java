package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

public class ConfigButtonDone {
    @SerializedName("is_delay")
    public boolean isDelay;
    @SerializedName("time_delay")
    public int timeDelay;
    @SerializedName("id_layout")
    public int idLayout;

    public boolean isDelay() {
        return isDelay;
    }

    public int getTimeDelay() {
        return timeDelay;
    }

    public int getIdLayout() {
        return idLayout;
    }
}
