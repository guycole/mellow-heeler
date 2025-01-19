package net.braingang.heeler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "onCreate");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean flag = wifiManager.startScan();
        if (flag) {
            Log.i(LOG_TAG, "scan started");
        } else {
            Log.i(LOG_TAG, "scan not started");
        }
    }

    private void parseScanResults(List<ScanResult> candidates) {
        Log.i(LOG_TAG, "scan results:" + candidates.size());

        for (int ndx = 0; ndx < candidates.size(); ndx++) {
            ScanResult temp = candidates.get(ndx);
            Log.i(LOG_TAG, "xxxxxxxxxxxx");
            Log.i(LOG_TAG, temp.BSSID);
            Log.i(LOG_TAG, temp.SSID);
            Log.i(LOG_TAG, temp.capabilities);
        }
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.i(LOG_TAG, "xxxxxxx onReceive xxxxxxxxxx");

            List<ScanResult> scanResults = wifiManager.getScanResults();
            if (scanResults.size() > 0) {
                parseScanResults(scanResults);
            } else {
                Log.i(LOG_TAG, "scan results empty");
            }
        }
    };
}