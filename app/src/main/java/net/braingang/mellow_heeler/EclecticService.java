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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class EclecticService extends IntentService {
    public static final String LOG_TAG = EclecticService.class.getName();

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

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                Log.i(LOG_TAG, "fresh location");
                if (location != null) {
                    // Logic to handle location object
                    Log.i(LOG_TAG, location.toString());
                } else {
                    Log.i(LOG_TAG, "fresh location is null");
                }
            }
        });
/*
        if (Personality.gracefulExit == false) {
            scheduleNextAlarm();
        }
 */
    }

    private void scheduleNextAlarm() {
        long delay = 59 * 1000L;

        Time timeNow = Utility.timeNow();
        Time timeAlarm = new Time();
        timeAlarm.set(timeNow.toMillis(Constant.IGNORE_DST) + delay);

        Intent intent = new Intent(this, EclecticService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, timeAlarm.toMillis(Constant.IGNORE_DST), alarmIntent);
    }
}