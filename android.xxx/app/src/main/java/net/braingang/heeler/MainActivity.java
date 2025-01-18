package net.braingang.heeler;

import androidx.appcompat.app.AppCompatActivity;

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

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private boolean runFlag;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<String>();
    private ArrayList<String> permissions = new ArrayList<String>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    HeelerService heelerService;

    public static final String LOG_TAG = MainActivity.class.getName();

    private void setRunMode(boolean flag) {
        Log.d("heeler", "set run mode");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        Button button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                Log.i(LOG_TAG, "xxxxxxx onClick xxxxxxxxxx");
                setRunMode(true);
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiManager.startScan();
                registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            }
        });

        Button button_stop = (Button) findViewById(R.id.button_stop);
        button_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vv) {
                setRunMode(false);
            }
        });
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm:wanted) {
           if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }

        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("heeler", "aaa");
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                Log.d("heeler", "bbb");

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            Log.d("heeler", "perms perms perms");
                            /*
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;

                             */
                        }
                    }

                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
        //heelerService.stopListener();
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

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {

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

//https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps