package com.example.medkit.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.example.medkit.R;

public class LoadingAlertDialog {
    private Activity mActivity;
    private AlertDialog mDialog;

    public LoadingAlertDialog(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void startAlertDialog() {
        View customViewDialog = LayoutInflater.from(mActivity).inflate(R.layout.activity_loading, null);
        mDialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(customViewDialog)
                .create();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.show();
        mDialog.getWindow().setLayout(300, 300);

    }

    public void dismissAlertDialog() {
        mDialog.dismiss();
    }
}
