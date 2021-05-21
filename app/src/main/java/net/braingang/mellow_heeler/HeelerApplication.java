package net.braingang.mellow_heeler;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class HeelerApplication extends Application {
    public static final String LOG_TAG = HeelerApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "xoxoxoxoxoxoxoxoxoxoxoxxoxoxoxoxoxo");
        Log.i(LOG_TAG, "xo start start start start start xo");
        Log.i(LOG_TAG, "xoxoxoxoxoxoxoxoxoxoxoxxoxoxoxoxoxo");

        startService(new Intent(this, EclecticService.class));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(LOG_TAG, "low memory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(LOG_TAG, "terminate");
    }

    @Override
    public void onTrimMemory(int arg) {
        super.onTrimMemory(arg);
        Log.i(LOG_TAG, "trim memory:" + arg);
    }
}
