package com.project.myapplication.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.project.myapplication.R;

public class CountDownService extends Service {
    private long timeLeftInMillis = 60000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, getNotification("Gửi lại sau: 60s"));
        startCountDown();
        return START_STICKY;
    }

    private void startCountDown() {
        CountDownTimer countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification("Gửi lại sau: " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                stopSelf();
            }
        }.start();
    }

    private Notification getNotification(String text) {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("countdown", "Countdown", NotificationManager.IMPORTANCE_LOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "countdown")
                .setContentTitle("Đếm ngược")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    private void updateNotification(String text) {
        Notification notification = getNotification(text);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
