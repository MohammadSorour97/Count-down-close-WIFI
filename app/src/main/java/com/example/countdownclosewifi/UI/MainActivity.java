package com.example.countdownclosewifi.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.countdownclosewifi.MyService;
import com.example.countdownclosewifi.R;
import com.example.countdownclosewifi.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BroadcastReceiver receiver;
    private Vibrator v;
    private String timeFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Intent filter to set the same action of the broadcast sender
        IntentFilter intentFilter_getRemainingTime = new IntentFilter();
        intentFilter_getRemainingTime.addAction("Count");

        // what will happen when the app receive a broadcast
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timeFormat = intent.getStringExtra("Show");
                binding.timeLeft.setText(timeFormat);
            }
        };
        registerReceiver(receiver, intentFilter_getRemainingTime);


        binding.start.setOnClickListener(view -> {
            String hours = binding.setTimeHours.getText().toString();
            String minutes = binding.setTimeMinutes.getText().toString();

            int ihours, iminutes;
            if (minutes.equals("0") && hours.equals("0") || minutes.isEmpty() && hours.isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter the duration", Toast.LENGTH_SHORT).show();
            } else if (!minutes.isEmpty() && !minutes.equals("0") && !hours.isEmpty() && !hours.equals("0")) {
                ihours = Integer.parseInt(binding.setTimeHours.getText().toString());
                iminutes = Integer.parseInt(binding.setTimeMinutes.getText().toString());

                Intent serviceIntent = new Intent(this, MyService.class);
                int time = ihours * 60 * 60 * 1000 + iminutes * 60 * 1000;
                serviceIntent.putExtra("Time", time);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);

                } else {
                    startService(serviceIntent);
                }
            } else if (!minutes.isEmpty() && !minutes.equals("0")) {
                iminutes = Integer.parseInt(binding.setTimeMinutes.getText().toString());
                Intent serviceIntent = new Intent(this, MyService.class);
                int time = iminutes * 60 * 1000;
                serviceIntent.putExtra("Time", time);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);

                } else {
                    startService(serviceIntent);
                }
            } else if (!hours.isEmpty() && !hours.equals("0")) {
                ihours = Integer.parseInt(binding.setTimeHours.getText().toString());
                Intent serviceIntent = new Intent(this, MyService.class);
                int time = ihours * 60 * 60 * 1000;
                serviceIntent.putExtra("Time", time);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);

                } else {
                    startService(serviceIntent);
                }
            }

        });

        binding.stop.setOnClickListener(view -> {
            if (timeFormat != null) {
                Intent stop = new Intent();
                stop.setAction("Stop");
                sendBroadcast(stop);

                Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                stopService(serviceIntent);

                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            } else {
                Toast.makeText(this, "Service is not running", Toast.LENGTH_SHORT).show();
            }


        });

        binding.exit.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}