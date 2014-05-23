package com.digiburo.mellow.heeler.lib;

import android.app.PendingIntent;

import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
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
   * true, use database on internal file system else external
   * production deployment is on external file system
   * unit tests execute on internal file system
   */
  private static Boolean databaseFileSystemFlag = false;

  public static synchronized Boolean isInternalDataBaseFileSystem() {
    return databaseFileSystemFlag;
  }

  public static synchronized void setInternalDataBaseFileSystem(boolean arg) {
    databaseFileSystemFlag = arg;
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
  private static SortieModel currentSortie;

  public static synchronized SortieModel getCurrentSortie() {
    return currentSortie;
  }

  public static synchronized void setCurrentSortie(SortieModel arg) {
    currentSortie = arg;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
