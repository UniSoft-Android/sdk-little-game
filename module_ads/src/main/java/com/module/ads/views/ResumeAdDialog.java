package com.module.ads.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.module.ads.R;

public class ResumeAdDialog extends Dialog {

    public ResumeAdDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resume_ad);
        funcStyle();
    }

    private void funcStyle() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setCancelable(true);
        window.setBackgroundDrawableResource(R.drawable.bg_dialog);
    }
}
