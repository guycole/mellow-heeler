package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * observation history
 * @author gsc
 */
public class ObservationModel implements DataBaseModelIf {
  private Long id;
  private String ssid;
  private String bssid;
  private String capability;
  private String locationUuid;
  private String sortieUuid;

  private boolean uploadFlag;

  private String timeStamp;
  private long timeStampMs;

  private int frequency;
  private int level;

  @Override
  public void setDefault(Context context) {
    locationUuid = Personality.getCurrentLocation().getLocationUuid().toString();
    sortieUuid = Personality.getCurrentSortie().getSortieId().toString();

    uploadFlag = false;

    Time timeNow = TimeUtility.timeNow();
    timeStamp = timeNow.format3339(false);
    timeStampMs = timeNow.toMillis(Constant.IGNORE_DST);

    ssid = "unknown";
    bssid = "unknown";
    capability = "unknown";

    uploadFlag = false;

    frequency = 0;
    level = 0;
  }

  public void setScanResult(ScanResult arg) {
    ssid = arg.SSID;
    bssid = arg.BSSID;
    capability = arg.capabilities;
    frequency = arg.frequency;
    level = arg.level;
  }

  @Override
  public ContentValues toContentValues(Context context) {
    ContentValues cv = new ContentValues();

    cv.put(ObservationTable.Columns.BSSID, bssid);
    cv.put(ObservationTable.Columns.CAPABILITY, capability);
    cv.put(ObservationTable.Columns.FREQUENCY, frequency);
    cv.put(ObservationTable.Columns.LEVEL, level);
    cv.put(ObservationTable.Columns.LOCATION_ID, locationUuid);
    cv.put(ObservationTable.Columns.SSID, ssid);
    cv.put(ObservationTable.Columns.SORTIE_ID, sortieUuid);
    cv.put(ObservationTable.Columns.TIME_STAMP, timeStamp);
    cv.put(ObservationTable.Columns.TIME_STAMP_MS, timeStampMs);

    if (uploadFlag) {
      cv.put(ObservationTable.Columns.UPLOAD_FLAG, Constant.SQL_TRUE);
    } else {
      cv.put(ObservationTable.Columns.UPLOAD_FLAG, Constant.SQL_FALSE);
    }

    return(cv);
  }

  @Override
  public void fromCursor(Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex(ObservationTable.Columns._ID));
    bssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.BSSID));
    capability = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.CAPABILITY));
    frequency = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.FREQUENCY));
    level = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.LEVEL));
    locationUuid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.LOCATION_ID));
    ssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.SSID));
    sortieUuid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.SORTIE_ID));
    timeStamp = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.TIME_STAMP));
    timeStampMs = cursor.getLong(cursor.getColumnIndex(ObservationTable.Columns.TIME_STAMP_MS));

    int temp = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.UPLOAD_FLAG));
    if (temp == Constant.SQL_TRUE) {
      uploadFlag = true;
    } else {
      uploadFlag = false;
    }
  }

  @Override
  public String getTableName() {
    return(ObservationTable.TABLE_NAME);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getSsid() {
    return ssid;
  }

  public String getBssid() {
    return bssid;
  }

  public String getCapability() {
    return capability;
  }

  public int getFrequency() {
    return frequency;
  }

  public int getLevel() {
    return level;
  }

  public long getTimeStampMs() {
    return timeStampMs;
  }

  public String getLocationUuid() {
    return locationUuid;
  }

  public String getSortieUuid() {
    return sortieUuid;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */