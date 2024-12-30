package net.braingang.heeler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HeelerService extends Service {
    public HeelerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}