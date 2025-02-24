package com.project.myapplication.view.components; // Đổi theo package của bạn

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.project.myapplication.R;

public class CustomProgressDialog {
    private AlertDialog progressDialog;

    public CustomProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        progressDialog = builder.create();
    }

    public void show() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
