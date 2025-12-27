package com.module.ads.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.module.ads.databinding.DialogUpdateAppBinding;
import com.module.ads.models.ConfigUpdate;
import com.module.ads.remote.FirebaseQuery;

public class UpdateAppDialog extends Dialog {
    private DialogUpdateAppBinding mBinding;
    private final Context mContext;
    private ConfigUpdate configUpdate;

    public UpdateAppDialog(@NonNull Context context, Context mContext) {
        super(context);
        this.mContext = mContext;
    }

    public UpdateAppDialog(@NonNull Context context, int themeResId, Context mContext) {
        super(context, themeResId);
        this.mContext = mContext;
    }

    public UpdateAppDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener, Context mContext) {
        super(context, cancelable, cancelListener);
        this.mContext = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initViews();
        onClickViews();
    }

    private void initViews() {
        configUpdate = FirebaseQuery.getConfigController().configUpdate;
        if (configUpdate != null) {
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            mBinding.tvVersionName.setText(configUpdate.getVersionName());
            mBinding.tvDescription.setText(configUpdate.getDescription());
            if (configUpdate.isCancel()) {
                mBinding.tvCancel.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvCancel.setVisibility(View.GONE);
            }
        }
    }

    private void onClickViews() {
        mBinding.tvUpdateNow.setOnClickListener(v -> {
            dismiss();
            if (configUpdate != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + configUpdate.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        mBinding.tvCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    public void showDialog() {
        if (configUpdate != null) {
            int versionCode = configUpdate.getVersionCode();
            if (getVersionApp() < versionCode) {
                show();
            }
        }
    }

    private int getVersionApp() {
        PackageInfo pInfo;
        int currentAppVersionCode = 1;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            if (pInfo != null) {
                currentAppVersionCode = pInfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentAppVersionCode;
    }
}
