package com.project.myapplication.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final View rootView;

    public NetworkChangeReceiver(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            Snackbar.make(rootView, "Đã kết nối mạng", Snackbar.ANIMATION_MODE_SLIDE).show();
        } else {
            Snackbar.make(rootView, "Không có kết nối mạng", Snackbar.ANIMATION_MODE_SLIDE).show();
        }
    }
}
