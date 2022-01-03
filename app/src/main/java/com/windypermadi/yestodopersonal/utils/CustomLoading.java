package com.windypermadi.yestodopersonal.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.RelativeLayout;

import com.windypermadi.yestodopersonal.R;

public class CustomLoading {
    public static CustomLoading customProgress = null;
    private Dialog mDialog;
    private RelativeLayout lini;

    public static CustomLoading getInstance() {
        if (customProgress == null) {
            customProgress = new CustomLoading();
        }
        return customProgress;
    }

    @SuppressLint("ResourceAsColor")
    public void showProgress(Context context, boolean cancelable) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparan);
        mDialog.setContentView(R.layout.custom_progress);
        lini = mDialog.findViewById(R.id.lini);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
