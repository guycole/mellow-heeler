package com.digiburo.mellow.heeler.lib;

import android.app.PendingIntent;
import android.location.Location;

import com.digiburo.mellow.heeler.lib.content.LocationModel;
import com.octo.android.robospice.SpiceManager;

/**
 * @author gsc
 */
public class Personality {

  /**
   * empty ctor to block instantiation
   */
  private Personality() {
    //empty
  }

  /**
   * alarm manager
   */
  private static PendingIntent alarmIntent;

  public static synchronized PendingIntent getAlarmIntent() {
    return alarmIntent;
  }

  public static synchronized void setAlarmIntent(PendingIntent arg) {
    alarmIntent = arg;
  }

  /**
   * spice manager
   */
  private static SpiceManager spiceManager;

  public static synchronized SpiceManager getSpiceManager() {
    return spiceManager;
  }

  public static synchronized void setSpiceManager(SpiceManager arg) {
    spiceManager = arg;
  }

  /**
   * application version
   */
  private static int applicationVersion;

  public static synchronized Integer getApplicationVersion() {
    return applicationVersion;
  }

  public static synchronized void setApplicationVersion(int arg) {
    applicationVersion = arg;
  }

  /**
   * current geographic location
   */
  private static LocationModel currentLocation;

  public static synchronized LocationModel getCurrentLocation() {
    return currentLocation;
  }

  public static synchronized void setCurrentLocation(LocationModel arg) {
    currentLocation = arg;
  }

  /**
   * current detection sortie
   */
  private static Sortie currentSortie;

  public static synchronized Sortie getCurrentSortie() {
    return currentSortie;
  }

  public static synchronized void setCurrentSortie(Sortie arg) {
    currentSortie = arg;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
