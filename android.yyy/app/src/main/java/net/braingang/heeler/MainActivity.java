package net.braingang.heeler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "xxxxxxx onCreate xxxxxxxxxx");

        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean flag = wifiManager.startScan();
        if (flag) {
            Log.i(LOG_TAG, "scan started");
        } else {
            Log.i(LOG_TAG, "scan not started");
        }

        Button button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                Log.i(LOG_TAG, "xxxxxxx onClick xxxxxxxxxx");
                registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        });

        Button button_stop = (Button) findViewById(R.id.button_stop);
        button_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                Log.i(LOG_TAG, "xxxxxxx onClick xxxxxxxxxx");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
        unregisterReceiver(scanReceiver);
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.i(LOG_TAG, "xxxxxxx onReceive xxxxxxxxxx");

            /*
            FileFacade fileFacade = new FileFacade();

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();

            if (scanResults.size() > 0) {
                Log.i(LOG_TAG, "scan results:" + scanResults.size());
                Observation observation = new Observation(Personality.locationResult, scanResults);

                fileFacade.writeObservation(observation, getApplicationContext());
            } else {
                Log.i(LOG_TAG, "scan results empty");
            }

            File[] files = fileFacade.getObservations(getApplicationContext());

            controlViewModel.setCounter(scanResults.size(), files.length);
             */
        }
    };
}