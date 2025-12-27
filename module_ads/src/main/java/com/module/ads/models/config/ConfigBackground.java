package com.module.ads.models.config;

import com.google.gson.annotations.SerializedName;

public class ConfigBackground {
    @SerializedName("color")
    public String color;
    @SerializedName("is_stroke")
    public boolean isStroke;
    @SerializedName("stroke_color")
    public String strokeColor;
    @SerializedName("stroke_size")
    public int strokeSize;
    @SerializedName("corner_radius")
    public int cornerRadius;

    public String getColor() {
        return color;
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
