package com.digiburo.mellow.heeler.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemClock;

import com.digiburo.mellow.heeler.lib.service.LocationService;
import com.digiburo.mellow.heeler.lib.service.ScanReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class TaskController {
  private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

  private AlarmManager alarmManager;

  private long timeOut = 60 * 1000L;

  /**
   *
   * @param context
   */
  public void startTask(Context context) {
    LOG.debug("startTask");

    gracefulShutDown(context);

    context.startService(new Intent(context, LocationService.class));

    Personality.setCurrentTask(new Task());

    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    wifiManager.startScan();

    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, ScanReceiver.class);
    intent.setAction(Constant.INTENT_ACTION_ALARM);
    PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timeOut, pending);
    Personality.setPendingIntent(pending);
  }

  /**
   *
   * @param context
   */
  public void stopTask(Context context) {
    LOG.debug("stopTask");
    gracefulShutDown(context);
  }

  /**
   *
   * @param context
   */
  private void gracefulShutDown(Context context) {
    context.stopService(new Intent(context, LocationService.class));

    if (Personality.getPendingIntent() != null) {
      alarmManager.cancel(Personality.getPendingIntent());
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */