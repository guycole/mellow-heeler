package net.braingang.mellow_heeler;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

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
/*
        //VagrantListener callback = (VagrantListener) getApplicationContext();
        VolleySingleton volleySingleton = VolleySingleton.getInstance(this);

        if ((Personality.currentTicket == null) || (Personality.currentTicket.getTicketId().length() < 1)) {
            Log.i(LOG_TAG, "skipping null or empty ticket");
        } else {
            String ticketState = Personality.currentTicket.getTicketState();

            switch(ticketState) {
                case "TICKET_CREATE":
                case "TICKET_ACCEPT":
                case "TICKET_TRIAL":
                    Log.i(LOG_TAG, "testing ticket id");
                    volleySingleton.ticketSelect(Personality.currentTicket.getTicketId(), null);
                    break;
                default:
                    Log.i(LOG_TAG, "skipping ticket test:" + ticketState);
            }
        }

        if (Personality.gracefulExit == false) {
            scheduleNextAlarm();
        }
 */
    }

    private void scheduleNextAlarm() {
        long delay = 59 * 1000L;

        Time timeNow = Utility.timeNow();
        Time timeAlarm = new Time();
        timeAlarm.set(timeNow.toMillis(Constants.IGNORE_DST) + delay);

        Intent intent = new Intent(this, EclecticService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, timeAlarm.toMillis(Constants.IGNORE_DST), alarmIntent);
    }
}