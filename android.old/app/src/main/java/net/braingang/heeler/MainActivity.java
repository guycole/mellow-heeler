package net.braingang.heeler;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import android.app.AlarmManager;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getName();

    public static final int PERMISSION_REQUEST = 12321;
    public static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 1234321;

    //LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_main);

        Button buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick");
            }
        });

        Button buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick");
            }
        });

        Button buttonUpload = (Button) findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "onClick");
                //DataBaseHelper.startUploadService(this);
            }
        });

        requestLocationPermission();

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //provider = locationManager.getBestProvider(new Criteria(), false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        if (api.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.i(LOG_TAG, "Google API noted");
        } else {
            Log.e(LOG_TAG, "Google API missing");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "onRequestPermissonsResult");

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //@AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        Log.i(LOG_TAG, "requestLocationPermission");

        String[] perms = {
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
        };

        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "permission noted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "grant all the things", PERMISSION_REQUEST, perms);
        }
    }
}