package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

public class ConfigButtonCta {
    @SerializedName("start_color")
    public String startColor;
    @SerializedName("end_color")
    public String endColor;
    @SerializedName("color_left_to_right")
    public boolean colorLeftToRight;
    @SerializedName("text_color")
    public String textColor;
    @SerializedName("is_stroke")
    public boolean isStroke;
    @SerializedName("stroke_color")
    public String strokeColor;
    @SerializedName("stroke_size")
    public int strokeSize;
    @SerializedName("corner_radius")
    public int cornerRadius;

    public String getStartColor() {
        return startColor;
    }

    public String getEndColor() {
        return endColor;
    }

    public boolean isColorLeftToRight() {
        return colorLeftToRight;
    }

    public String getTextColor() {
        return textColor;
    }

    public boolean isStroke() {
        return isStroke;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public int getStrokeSize() {
        return strokeSize;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }
}
