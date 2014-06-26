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
  public static final String TEST_INSTALLATION_ID = "bcf7210f-2ab3-4a66-9cf9-256687d99c46";
  public static final String TEST_SORTIE_ID = "bcf7210f-2ab3-4a66-9cf9-256687d99c46";

  //
  public static final String PRODUCTION_CONFIGURATION_URL = "http://digiburo.com/hal/mellow-heeler1.json";

  //
  public static final String TEST_CONFIGURATION_URL = "http://digiburo.com/hal/mellow-heeler-test1.json";
  public static final String TEST_URL_FRAGMENT = "mellow-heeler-test";

  //
  public static final String DIAGNOSTIC_URL = "https://mellow-heeler.appspot.com/diagnostic";

  //
  public static final String EMPTY = "Empty";
  public static final String OK = "OK";
  public static final String UNKNOWN = "Unknown";

  // MenuActivity
  public static final String INTENT_ACTION_ABOUT = "INTENT_ACTION_ABOUT";
  public static final String INTENT_ACTION_ALARM = "INTENT_ACTION_ALARM";
  public static final String INTENT_ACTION_CLEAN = "INTENT_ACTION_CLEAN";
  public static final String INTENT_ACTION_SETTING = "INTENT_ACTION_SETTING";
  public static final String INTENT_ACTION_SAY_THIS = "INTENT_ACTION_SAY_THIS";
  public static final String INTENT_ACTION_UPLOAD = "INTENT_ACTION_UPLOAD";

  // UI Update
  public static final String CONSOLE_UPDATE = "com.digiburo.mellow.heeler.update";
  public static final String CLEAN_EVENT = "com.digiburo.mellow.heeler.clean";
  public static final String PROVIDER_EVENT = "com.digiburo.mellow.heeler.provider";
  public static final String UPLOAD_EVENT = "com.digiburo.mellow.heeler.upload";

  // RoboSpice update
  //public static final String JSON_RESPONSE = "com.digiburo.mellow.heeler.json_response";

  //
  public static final String INTENT_AUTH_FLAG = "INTENT_AUTH_FLAG";
  public static final String INTENT_COMPLETE_FLAG = "INTENT_COMPLETE_FLAG";
  public static final String INTENT_MODE_FLAG = "INTENT_MODE_FLAG";
  public static final String INTENT_JSON_RESPONSE = "INTENT_JSON_RESPONSE";
  public static final String INTENT_JSON_TYPE = "INTENT_JSON_TYPE";
  public static final String INTENT_LOCATION_UPDATE = "INTENT_LOCATION_UPDATE";
  public static final String INTENT_LOCATION_UUID = "INTENT_LOCATION_UUID";
  public static final String INTENT_OBSERVATION_UPDATE = "INTENT_OBSERVATION_UPDATE";
  public static final String INTENT_OBSERVATION_UUID = "INTENT_OBSERVATION_UUID";
  public static final String INTENT_PROVIDER_FLAG = "INTENT_PROVIDER_FLAG";
  public static final String INTENT_ROW_KEY = "INTENT_ROW_KEY";
  public static final String INTENT_SAY_THIS = "INTENT_SAY_THIS";
  public static final String INTENT_SORTIE_UUID = "INTENT_SORTIE_UUID";
  public static final String INTENT_STATUS_FLAG = "INTENT_STATUS_FLAG";
  public static final String INTENT_UPLOAD_COUNT = "INTENT_UPLOAD_COUNT";
  public static final String INTENT_UPLOAD_TYPE = "INTENT_UPLOAD_TYPE";

  //
  public static final int NOTIFICATION_ID = 2718;

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
