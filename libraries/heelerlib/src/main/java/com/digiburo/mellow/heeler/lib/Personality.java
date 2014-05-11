package com.digiburo.mellow.heeler.lib;

import android.app.PendingIntent;
import android.location.Location;

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
  private static PendingIntent pendingIntent;

  public static synchronized PendingIntent getPendingIntent() {
    return pendingIntent;
  }

  public static synchronized void setPendingIntent(PendingIntent arg) {
    pendingIntent = arg;
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
  private static Location currentLocation;

  public static synchronized Location getCurrentLocation() {
    return currentLocation;
  }

  public static synchronized void setCurrentLocation(Location arg) {
    currentLocation = arg;
  }

  /**
   * current collection task
   */
  private static Task currentTask;

  public static synchronized Task getCurrentTask() {
    return currentTask;
  }

  public static synchronized void setCurrentTask(Task arg) {
    currentTask = arg;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
