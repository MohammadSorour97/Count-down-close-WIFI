package com.example.countdownclosewifi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.countdownclosewifi.UI.MainActivity;

import java.util.Locale;


public class MyService extends Service {
    private WifiManager wifiManager;
    private int timeRemaining;

    private static final String channel_id = "Notification Channel";
    private BroadcastReceiver broadcastReceiver;
    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //make notification channel to show notification if os is higher than android Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notification";
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Stop");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                countDownTimer.cancel();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);


        timeRemaining = intent.getIntExtra("Time", 0);

        countDownTimer = new CountDownTimer(timeRemaining + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String timeLeftFormatted = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
                showNotification(timeLeftFormatted);
                Intent intent = new Intent();
                intent.setAction("Count");
                intent.putExtra("Show", timeLeftFormatted);
                sendBroadcast(intent);
                timeRemaining -= 1000;
            }

            @Override
            public void onFinish() {
                wifiManager.setWifiEnabled(false);
                Intent intent = new Intent();
                intent.setAction("Count");
                intent.putExtra("Show", timeRemaining);
                sendBroadcast(intent);
                stopSelf();
            }
        }.start();

//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//
//                if (timeRemaining == 0) {
//                    wifiManager.setWifiEnabled(false);
//                    timer.cancel();
//                    stopSelf();
//                }
//
//
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
////                    startMyOwnForeground(timeRemaining);
////                else
////                    startForeground(1, new Notification());
//
//                showNotification(timeRemaining);
//                Intent intent = new Intent();
//                intent.setAction("Count");
//                intent.putExtra("Show", timeRemaining);
//                sendBroadcast(intent);
//
//                timeRemaining -= 1;
//
//            }
//        }, 0, 1000);

        return START_STICKY;
    }


//    private void startMyOwnForeground(int timeRemaining) {
//        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
//        String channelName = "My Background Service";
//        NotificationChannel chan = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            assert manager != null;
//            manager.createNotificationChannel(chan);
//        }
//
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder
//                .setSmallIcon(R.drawable.add_circle_24)
//                .setContentTitle("App is running in background")
////                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
////                .setCategory(Notification.CATEGORY_SERVICE)
//                .setContentText(String.valueOf(timeRemaining))
////                .setOnlyAlertOnce(true)
//                .build();
//        startForeground(2, notification);
//    }


    private void showNotification(String timeLeftFormatted) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification;
        // VERY IMPORTANT *** Notification ID must be same as Notification Channel ID
        // DON'T FORGET THAT
        // NEVER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channel_id)
                    .setContentTitle("You are running out of time")
                    .setContentText(timeLeftFormatted)
//                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.wifi_24)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
//                    .setOngoing(true)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, channel_id)
                    .setContentTitle("You are running out of time")
                    .setContentText(timeLeftFormatted)
                    .setSmallIcon(R.drawable.wifi_24)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .build();
        }

        // Notification ID cannot be 0.
        startForeground(12, notification);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        stopSelf();
        unregisterReceiver(broadcastReceiver);
    }
}
