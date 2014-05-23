package com.digiburo.mellow.heeler.lib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * invoked to process WiFi scan results (start scanning or collect results)
 * @author gsc
 */
public class ScanReceiver extends BroadcastReceiver {
  private static final Logger LOG = LoggerFactory.getLogger(ScanReceiver.class);

  /**
   *
   * @param context
   * @param intent
   */
  public void onReceive(Context context, Intent intent) {
    LOG.debug("xxx xxx onReceive xxx xxx:" + intent.getAction());

    SortieModel sortieModel = Personality.getCurrentSortie();
    if (sortieModel == null) {
      LOG.debug("no sortie - ignoring intent");
      return;
    }

    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel == null) {
      LOG.debug("unknown location skipping observation");
      return;
    }

    if (Constant.INTENT_ACTION_ALARM.equals(intent.getAction())) {
      LOG.debug("start wifi scan");
      WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      wifiManager.startScan();
      return;
    }

    long rowKey = 0;
    DataBaseFacade dataBaseFacade = new DataBaseFacade(context);

    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    List<ScanResult> scanList = wifiManager.getScanResults();
    for (ScanResult scanResult:scanList) {
      ObservationModel observationModel = new ObservationModel();
      observationModel.setDefault();
      observationModel.setScanResult(scanResult, locationModel.getLocationUuid(), sortieModel.getSortieUuid());
      dataBaseFacade.insert(observationModel, context);
      rowKey = observationModel.getId();
    }

    if (rowKey < 1) {
      LOG.debug("empty scanlist");
    } else {
      LOG.debug("broadcast scan update");
      Intent notifier = new Intent(Constant.FRESH_UPDATE);
      notifier.putExtra(Constant.INTENT_ROW_KEY, rowKey);
      context.sendBroadcast(notifier);
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
