package com.module.ads.models;

import com.google.gson.annotations.SerializedName;

public class ConfigUpdate {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("version_name")
    private String versionName;
    @SerializedName("version_code")
    private int versionCode;
    @SerializedName("package_name")
    private String packageName;
    @SerializedName("is_cancel")
    private boolean isCancel;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isCancel() {
        return isCancel;
    }
}
