package com.digiburo.mellow.heeler.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemClock;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.service.LocationService;
import com.digiburo.mellow.heeler.lib.service.ScanReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage WiFi detection/collection.
 * @author gsc
 */
public class SortieController {
  private static final Logger LOG = LoggerFactory.getLogger(SortieController.class);

  private AlarmManager alarmManager;

  private long timeOut = 60 * 1000L;

  /**
   * create/start a fresh sortie
   * @param sortieName
   * @param context
   */
  public void startSortie(final String sortieName, final Context context) {
    LOG.debug("startSortie");

    SortieModel sortieModel = new SortieModel();
    sortieModel.setDefault();
    sortieModel.setSortieName(sortieName);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
    dataBaseFacade.insert(sortieModel, context);

    Personality.setCurrentSortie(sortieModel);

    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    if (Personality.getAlarmIntent() != null) {
      alarmManager.cancel(Personality.getAlarmIntent());
    }

    context.startService(new Intent(context, LocationService.class));

    Intent intent = new Intent(context, ScanReceiver.class);
    intent.setAction(Constant.INTENT_ACTION_ALARM);
    PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timeOut, pending);
    Personality.setAlarmIntent(pending);

    Intent notifier = new Intent(Constant.FRESH_UPDATE);
    notifier.putExtra(Constant.INTENT_MODE_FLAG, true);
    context.sendBroadcast(notifier);
  }

  /**
   * terminate existing sortie
   * @param context
   */
  public void stopSortie(final Context context) {
    LOG.debug("stopSortie");
    context.stopService(new Intent(context, LocationService.class));

    if (Personality.getAlarmIntent() != null) {
      alarmManager.cancel(Personality.getAlarmIntent());
    }

    Intent notifier = new Intent(Constant.FRESH_UPDATE);
    notifier.putExtra(Constant.INTENT_MODE_FLAG, false);
    context.sendBroadcast(notifier);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */