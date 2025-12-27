package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

public class ConfigScreenLanguage {
    @SerializedName("button_done")
    public ConfigButtonDone buttonDone;

    public ConfigButtonDone getButtonDone() {
        return buttonDone;
    }

    public void setButtonDone(ConfigButtonDone buttonDone) {
        this.buttonDone = buttonDone;
    }
}
