package com.digiburo.mellow.heeler.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.service.LocationService;
import com.digiburo.mellow.heeler.lib.service.ScanReceiver;
import com.digiburo.mellow.heeler.lib.service.SpeechService;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage WiFi detection/collection.
 * @author gsc
 */
public class SortieController {
  private static final Logger LOG = LoggerFactory.getLogger(SortieController.class);

  private AlarmManager alarmManager;

  /**
   * create/start a fresh sortie
   * @param sortieName
   * @param context
   */
  public void startSortie(final String sortieName, final Context context) {
    LOG.debug("startSortie");

    Personality.setCurrentSortie(createSortie(sortieName, context));

    LocationService.startLocationService(context);

    cancelAlarm(context);
    startAlarm(context);

    broadcastChange(true, context);

    SpeechService.sayThis("sortie start", context);
  }

  /**
   * terminate existing sortie
   * @param context
   */
  public void stopSortie(final Context context) {
    LOG.debug("stopSortie");

    LocationService.stopLocationService(context);

    cancelAlarm(context);

    broadcastChange(false, context);

    Intent notifier = new Intent(Constant.FRESH_UPDATE);
    notifier.putExtra(Constant.INTENT_MODE_FLAG, false);
    context.sendBroadcast(notifier);

    SpeechService.sayThis("sortie stop", context);
  }

  private void broadcastChange(boolean startFlag, Context context) {
    Intent notifier = new Intent(Constant.FRESH_UPDATE);
    notifier.putExtra(Constant.INTENT_MODE_FLAG, startFlag);
    context.sendBroadcast(notifier);
  }

  private SortieModel createSortie(final String sortieName, final Context context) {
    SortieModel sortieModel = new SortieModel();
    sortieModel.setDefault();
    sortieModel.setSortieName(sortieName);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
    dataBaseFacade.insert(sortieModel, context);

    return sortieModel;
  }

  private void cancelAlarm(final Context context) {
    if (Personality.getAlarmIntent() != null) {
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmManager.cancel(Personality.getAlarmIntent());
      Personality.setAlarmIntent(null);
    }
  }

  private void startAlarm(final Context context) {
    Intent intent = new Intent(context, ScanReceiver.class);
    intent.setAction(Constant.INTENT_ACTION_ALARM);
    PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, 0);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), getPollFrequency(context), pending);
    Personality.setAlarmIntent(pending);
  }

  private long getPollFrequency(Context context) {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    String temp = userPreferenceHelper.getPollFrequency(context);
    long result = Long.parseLong(temp) * 1000L;
    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */