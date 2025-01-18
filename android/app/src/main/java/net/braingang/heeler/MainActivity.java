package net.braingang.heeler;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, "xxxxxxx onCreate xxxxxxxxxx");

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        boolean flag = wifiManager.startScan();
        if (flag) {
            Log.i(LOG_TAG, "scan started");
        } else {
            Log.i(LOG_TAG, "scan not started");
        }
/*

public void registerScanResultsCallback (Executor executor,
                WifiManager.ScanResultsCallback callback);

public List<ScanResult> getScanResults ();

public void unregisterScanResultsCallback (WifiManager.ScanResultsCallback callback);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }

 */
    }

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            Log.i(LOG_TAG, "xxxxxxx onReceive xxxxxxxxxx");
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                Log.i(LOG_TAG, "xxxxxxx success xxxxxxxxxx");
                //scanSuccess();
            } else {
                // scan failure handling
                //scanFailure();
                Log.i(LOG_TAG, "xxxxxxx failure xxxxxxxxxx");
            }
        }
    };
}