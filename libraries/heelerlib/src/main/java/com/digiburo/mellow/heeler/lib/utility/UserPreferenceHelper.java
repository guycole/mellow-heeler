package com.digiburo.mellow.heeler.lib.utility;

import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * convenience routines to manage user preference database
 *
 * @author gsc
 */
public class UserPreferenceHelper {

  //
  private static final Logger LOG = LoggerFactory.getLogger(UserPreferenceHelper.class);

  //true, audio cues enabled
  public static final String USER_PREF_AUDIO_CUE = "audioCue";

  //first run timestamp
  public static final String USER_PREF_FIRST_RUN_TIMESTAMP = "firstRunTimeStamp";

  //installation identifier
  public static final String USER_PREF_INSTALL_ID = "installId";

  //WiFi scan interval
  public static final String USER_PREF_POLL_FREQUENCY = "pollFrequency";

  //web service root version
  public static final String USER_PREF_WS_CONFIG_VERSION = "wsConfigVersion";

  //URL from web service
  public static final String USER_PREF_WS_LOCATION_URL = "wsLocationUrl";
  public static final String USER_PREF_WS_OBSERVATION_URL = "wsObservationUrl";

  /**
   * ctor
   * @param context
   */
  public UserPreferenceHelper(Context context) {
    if (isEmptyPreferences(context)) {
      writeDefaults(context);
    }

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    sp.registerOnSharedPreferenceChangeListener(listener);
  }

  /**
   * seed default database - should only invoke once for fresh install
   * @param context
   */
  private void writeDefaults(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();

    editor.putBoolean(USER_PREF_AUDIO_CUE, true);

    editor.putString(USER_PREF_INSTALL_ID, UUID.randomUUID().toString());

    editor.putLong(USER_PREF_FIRST_RUN_TIMESTAMP, TimeUtility.timeMillis());

    editor.putString(USER_PREF_POLL_FREQUENCY, "60");

    editor.putInt(USER_PREF_WS_CONFIG_VERSION, 0);
    editor.putString(USER_PREF_WS_LOCATION_URL, "");
    editor.putString(USER_PREF_WS_OBSERVATION_URL, "");

    editor.commit();
  }
  
  /**
   * Could only be true on a fresh install
   * @param context
   * @return true if user preferences are empty
   */
  public boolean isEmptyPreferences(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    Map<String, ?> map = sp.getAll();
    return(map.isEmpty());
  }

  /**
   * determine if audio cue enabled
   * @param context
   * @return true, audio cue enabled
   */
  public boolean isAudioCue(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getBoolean(USER_PREF_AUDIO_CUE, true));
  }
  
  /**
   * define if audio cue enabled
   * @param context
   * @param arg true, audio cue enabled
   */
  public void setAudioCue(Context context, boolean arg) {
    setPreference(context, USER_PREF_AUDIO_CUE, arg);
  }

  /**
   * return WiFi scan interval in seconds, must be String to pacify PreferenceFragment
   * @param context
   * @return WiFi scan interval in seconds
   */
  public String getPollFrequency(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getString(USER_PREF_POLL_FREQUENCY, "0"));
  }

  /**
   * define WiFi scan interval in seconds, must be String to pacify PreferenceFragment
   * @param context
   * @param WiFi scan interval in seconds
   */
  public void setPollFrequency(Context context, String arg) {
    setPreference(context, USER_PREF_POLL_FREQUENCY, arg);
  }

  /**
   * return web service config version
   * @param context
   * @return web service config version
   */
  public Integer getWebServiceConfigVersion(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getInt(USER_PREF_WS_CONFIG_VERSION, 0));
  }

  /**
   * define web service config version
   * @param context
   * @param web service config version
   */
  public void setWebServiceConfigVersion(Context context, int arg) {
    setPreference(context, USER_PREF_WS_CONFIG_VERSION, arg);
  }

  /**
   * URL to POST locations
   * @param context
   * @return location URL or empty string if undefined
   */
  public String getLocationUrl(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getString(USER_PREF_WS_LOCATION_URL, ""));
  }

  /**
   * define URL to POST locations
   * @param context
   * @param arg location URL
   */
  public void setLocationUrl(Context context, String arg) {
    setPreference(context, USER_PREF_WS_LOCATION_URL, arg);
  }

  /**
   * URL to POST observations
   * @param context
   * @return observation URL or empty string if undefined
   */
  public String getObservationUrl(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getString(USER_PREF_WS_OBSERVATION_URL, ""));
  }

  /**
   * define URL to POST observations
   * @param context
   * @param arg observation URL
   */
  public void setObservationUrl(Context context, String arg) {
    setPreference(context, USER_PREF_WS_OBSERVATION_URL, arg);
  }

  /**
   * Installation Identifier, generated for fresh install
   * @param context
   * @return installation ID or empty string if undefined
   */
  public String getInstallationId(Context context) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    return(sp.getString(USER_PREF_INSTALL_ID, ""));
  }

  /**
   * update preferences file
   * @param context context
   * @param key key
   * @param arg value
   */
  private void setPreference(Context context, String key, boolean arg) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putBoolean(key, arg);
    editor.commit();
  }

  /**
   * update preferences file
   * @param context context
   * @param key key
   * @param arg value
   */
  private void setPreference(Context context, String key, int arg) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putInt(key, arg);
    editor.commit();
  }

  /**
   * update preferences file
   * @param context context
   * @param key key
   * @param arg value
   */
  private void setPreference(Context context, String key, long arg) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putLong(key, arg);
    editor.commit();
  }

  /**
   * update preferences file
   * @param context context
   * @param key key
   * @param arg value
   */
  private void setPreference(Context context, String key, String arg) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(key, arg);
    editor.commit();
  }

  /**
   * 
   */
  private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      LOG.debug("shared preference change:" + key);

      /*
      if (key.equals(USER_PREF_GCM_ENABLED)) {

      } else if (key.equals(USER_PREF_GCM_REGISTERED)) {

      }
      */
    }
  };
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
