package net.braingang.mellow_heeler;

import android.text.format.Time;

public class Utility {

    public static Time timeNow() {
        Time time = new Time();
        time.setToNow();
        return time;
    }

    public static long timeMillis(Time arg) {
        return arg.toMillis(Constant.IGNORE_DST);
    }
}
