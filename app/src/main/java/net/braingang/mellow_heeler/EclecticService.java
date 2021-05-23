package net.braingang.mellow_heeler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.format.Time;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class EclecticService extends IntentService {
    public static final String LOG_TAG = EclecticService.class.getName();

    public static final int REQUEST_CODE = 6789;

    public EclecticService() {
        super("EclecticService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "-x-x-x-x-x-x-x-x-x- onHandleIntent -x-x-x-x-x-x-x-x-x-");

        if (LocationResult.hasResult(intent)) {
            Log.i(LOG_TAG, "has result true");
            LocationResult locationResult = LocationResult.extractResult(intent);
            Log.i(LOG_TAG, "result:" + locationResult);
        } else {
            Log.i(LOG_TAG, "has result false");
        }
    }
}