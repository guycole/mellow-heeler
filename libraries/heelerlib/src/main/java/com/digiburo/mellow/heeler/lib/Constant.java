package com.digiburo.mellow.heeler.lib;

/**
 * application constants
 *
 * @author gsc
 */
public class Constant {

  /**
   * empty ctor to block instantiation
   */
  private Constant() {
    //empty
  }

  //
  public static final String DEFAULT_SORTIE_NAME = "Default Sortie";

  //
  public static final String DEFAULT_SSID = "MH Default SSID";

  //
  public static final String AUTHORITY = "com.digiburo.mellow.heeler.authority";

  //
  public static final String DIAGNOSTIC_URL = "https://mellow-heeler.appspot.com/diagnostic";

  public static final String OK = "OK";
  public static final String UNKNOWN = "Unknown";

  //
  public static final String INTENT_ACTION_ABOUT = "INTENT_ACTION_ABOUT";
  public static final String INTENT_ACTION_ALARM = "INTENT_ACTION_ALARM";
  public static final String INTENT_ACTION_CLEAN = "INTENT_ACTION_CLEAN";
  public static final String INTENT_ACTION_PREFERENCE = "INTENT_ACTION_PREFERENCE";
  public static final String INTENT_ACTION_UPLOAD = "INTENT_ACTION_UPLOAD";

  //
  public static final String INTENT_AUTH_FLAG = "INTENT_AUTH_FLAG";
  public static final String INTENT_ROW_KEY = "INTENT_ROW_KEY";
  public static final String INTENT_MODE_FLAG = "INTENT_MODE_FLAG";
  public static final String INTENT_UPLOAD_FLAG = "INTENT_UPLOAD_FLAG";

  // intent filter
  public static final String FRESH_UPDATE = "com.digiburo.mellow.heeler.update";
  public static final String CLEAN_EVENT = "com.digiburo.mellow.heeler.clean";
  public static final String UPLOAD_EVENT = "com.digiburo.mellow.heeler.upload";

  //
  public static final Integer SQL_TRUE = 1;
  public static final Integer SQL_FALSE = 0;

  //
  public static final double EPSILON = 0.000001;

  // daylight savings time is not important
  public static final boolean IGNORE_DST = false;
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
