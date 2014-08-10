package com.digiburo.mellow.heeler.lib;

import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.utility.LegalMode;
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
   * true, use database on internal file system else external
   * production deployment is on external file system
   * unit tests must execute on internal file system
   */
  private static Boolean databaseFileSystemFlag = false;

  public static synchronized Boolean isInternalDataBaseFileSystem() {
    return databaseFileSystemFlag;
  }

  public static synchronized void setInternalDataBaseFileSystem(boolean arg) {
    databaseFileSystemFlag = arg;
  }

  /**
   * true, gps provider enabled
   */
  private static Boolean gpsProviderFlag = false;

  public static synchronized Boolean isGpsProvider() {
    return gpsProviderFlag;
  }

  public static synchronized void setGpsProvider(boolean arg) {
    gpsProviderFlag = arg;
  }

  /**
   * remote configuration URL, default value is for production, changes for unit test
   */
  private static String remoteConfigurationUrl = Constant.PRODUCTION_CONFIGURATION_URL;
//  private static String remoteConfigurationUrl = Constant.TEST_CONFIGURATION_URL;

  public static synchronized String getRemoteConfigurationUrl() {
    return remoteConfigurationUrl;
  }

  public static synchronized void setRemoteConfigurationUrl(String arg) {
    remoteConfigurationUrl = arg;
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

  /**
   * currently observed WAP
   */
  private static ObservationModelList currentObservedModelList;

  public static synchronized ObservationModelList getCurrentObserved() {
    return currentObservedModelList;
  }

  public static synchronized void setCurrentObserved(ObservationModelList arg) {
    currentObservedModelList = arg;
  }

  /**
   * current operational mode
   */
  private static LegalMode legalMode = LegalMode.UNKNOWN;

  public static synchronized LegalMode getOperationMode() {
    return legalMode;
  }

  public static synchronized void setOperationMode(LegalMode arg) {
    legalMode = arg;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */