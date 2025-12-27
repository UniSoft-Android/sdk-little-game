package com.module.ads.admob.inters;

import android.app.Activity;

import com.module.ads.views.LoadingAdDialog;


public class IntersUtils {
    private static LoadingAdDialog loadingAdDialog;

    public static void showDialogLoading(Activity activity) {
        try {
            if (loadingAdDialog == null) {
                loadingAdDialog = new LoadingAdDialog(activity);
            }
            loadingAdDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialogLoading() {
        try {
            if (loadingAdDialog != null && loadingAdDialog.isShowing()) {
                loadingAdDialog.dismiss();
                loadingAdDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
