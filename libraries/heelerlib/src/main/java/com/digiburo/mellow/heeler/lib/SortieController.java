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
public class SortieController {
  private static final Logger LOG = LoggerFactory.getLogger(SortieController.class);

  private AlarmManager alarmManager;

  private long timeOut = 60 * 1000L;

  /**
   *
   * @param context
   */
  public void startSortie(Context context) {
    LOG.debug("startSortie");

    gracefulShutDown(context);

    Personality.setCurrentSortie(new Sortie());

    context.startService(new Intent(context, LocationService.class));

    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    wifiManager.startScan();

    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(context, ScanReceiver.class);
    intent.setAction(Constant.INTENT_ACTION_ALARM);
    PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timeOut, pending);
    Personality.setAlarmIntent(pending);
  }

  /**
   *
   * @param context
   */
  public void stopSortie(Context context) {
    LOG.debug("stopSortie");
    gracefulShutDown(context);
  }

  /**
   *
   * @param context
   */
  private void gracefulShutDown(Context context) {
    context.stopService(new Intent(context, LocationService.class));

    if (Personality.getAlarmIntent() != null) {
      alarmManager.cancel(Personality.getAlarmIntent());
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */