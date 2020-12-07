package com.example.countdownclosewifi.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.countdownclosewifi.MyService;
import com.example.countdownclosewifi.R;
import com.example.countdownclosewifi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Intent filter to set the same action of the broadcast sender
        IntentFilter intentFilter_getRemainingTime = new IntentFilter();
        intentFilter_getRemainingTime.addAction("Count");

        // what will happen when the app receive a broadcast
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int i = intent.getIntExtra("Show", 0);
                binding.timeLeft.setText(String.valueOf(i));
            }
        };
        registerReceiver(receiver, intentFilter_getRemainingTime);


        binding.start.setOnClickListener(view -> {
            if (binding.setTime.getText().toString().isEmpty() || Long.parseLong(binding.setTime.getText().toString()) == 0) {
                Toast.makeText(MainActivity.this, "Enter the duration", Toast.LENGTH_SHORT).show();
            } else {
                Intent serviceIntent = new Intent(this, MyService.class);
                int time = Integer.parseInt(binding.setTime.getText().toString());
                serviceIntent.putExtra("Time", time);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);

                } else {
                    startService(serviceIntent);
                }

            }

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}