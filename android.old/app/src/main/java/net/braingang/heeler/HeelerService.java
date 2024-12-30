package net.braingang.heeler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

import androidx.core.app.JobIntentService;

import net.braingang.heeler.Constant;
import net.braingang.heeler.TimeUtility;

public class HeelerService extends JobIntentService {
    public static final String LOG_TAG = HeelerService.class.getName();

    public static final int JOB_ID = 4657;

    @Override
    protected void onHandleWork(Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        Log.i(LOG_TAG, "Executing work: " + intent);

        Log.i(LOG_TAG, "-x-x-x-x-x-x-x-x-x- onStartCommand -x-x-x-x-x-x-x-x-x-");

        /*
        if (!Personality.gracefulExit) {
            scheduleNextAlarm();
        }
         */
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
    }

    private void scheduleNextAlarm() {
        long delay = Constant.ONE_MINUTE;

        Time timeNow = TimeUtility.timeNow();
        Time timeAlarm = new Time();
        timeAlarm.set(timeNow.toMillis(Constant.IGNORE_DST) + delay);

        Intent intent = new Intent(this, HeelerService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, timeAlarm.toMillis(Constant.IGNORE_DST), alarmIntent);
    }
}