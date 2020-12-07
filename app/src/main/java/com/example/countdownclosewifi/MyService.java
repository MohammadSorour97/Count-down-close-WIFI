package com.example.countdownclosewifi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
//import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.countdownclosewifi.UI.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private WifiManager wifiManager;
    private int timeRemaining;
    private Timer timer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        timeRemaining = intent.getIntExtra("Time", 0);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private static final String TAG = "sss";

            @Override
            public void run() {

                if (timeRemaining == 0) {
                    wifiManager.setWifiEnabled(false);
                    timer.cancel();
                    stopSelf();
                }

                showNotification(timeRemaining);

                Log.e(TAG, "run: test");

                Intent intent = new Intent();
                intent.setAction("Count");
                intent.putExtra("Show", timeRemaining);
                sendBroadcast(intent);

                timeRemaining -= 1;


            }
        }, 0, 1000);

        return START_STICKY;
    }


    private void showNotification(int timeRemaining) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String channel_id = "id";
        Notification notification;
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channel_id)
                    .setContentTitle("You are running out of time")
                    .setContentText(String.valueOf(timeRemaining))
                    .setSmallIcon(R.drawable.wifi_24)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, channel_id)
                    .setContentTitle("You are running out of time")
                    .setContentText(String.valueOf(timeRemaining))
                    .setSmallIcon(R.drawable.wifi_24)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .build();
        }

        // Notification ID cannot be 0.
       startForeground(12,notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "Notification Channel";
            CharSequence name = "notification";
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }



    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
    }
}
