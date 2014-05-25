package com.digiburo.mellow.heeler.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
  private String observationUuid;
  private String sortieUuid;

  private boolean uploadFlag;

  private String timeStamp;
  private long timeStampMs;

  private int frequency;
  private int level;

  @Override
  public void setDefault() {
    id = 0L;
    uploadFlag = false;

    locationUuid = UUID.randomUUID().toString();
    observationUuid = UUID.randomUUID().toString();
    sortieUuid = UUID.randomUUID().toString();

    Time timeNow = TimeUtility.timeNow();
    timeStamp = timeNow.format3339(false);
    timeStampMs = timeNow.toMillis(Constant.IGNORE_DST);

    ssid = Constant.UNKNOWN;
    bssid = Constant.UNKNOWN;
    capability = Constant.UNKNOWN;

    frequency = 0;
    level = 0;
  }

  /**
   *
   * @param ssid
   * @param bssid
   * @param capability
   * @param frequency
   * @param level
   * @param locationId
   * @param sortieId
   */
  public void setScanResult(final String ssid, final String bssid, final String capability, int frequency, int level, final String locationId, final String sortieId) {
    if (ssid == null) {
      this.ssid = Constant.DEFAULT_SSID;
    } else {
      String trimSsid = ssid.trim();
      if (trimSsid.isEmpty()) {
        this.ssid = Constant.DEFAULT_SSID;
      } else {
        this.ssid = trimSsid;
      }
    }

    this.bssid = bssid;
    this.capability = capability;
    this.frequency = frequency;
    this.level = level;

    locationUuid = locationId;
    sortieUuid = sortieId;
  }

  /**
   *
   * @param arg
   * @param locationId
   * @param sortieId
   */
  public void setScanResult(final ScanResult arg, final String locationId, final String sortieId) {
    setScanResult(arg.SSID, arg.BSSID, arg.capabilities, arg.frequency, arg.level, locationId, sortieId);
  }

  @Override
  public ContentValues toContentValues() {
    ContentValues cv = new ContentValues();

    cv.put(ObservationTable.Columns.BSSID, bssid);
    cv.put(ObservationTable.Columns.CAPABILITY, capability);
    cv.put(ObservationTable.Columns.FREQUENCY, frequency);
    cv.put(ObservationTable.Columns.LEVEL, level);
    cv.put(ObservationTable.Columns.LOCATION_ID, locationUuid);
    cv.put(ObservationTable.Columns.OBSERVATION_ID, observationUuid);
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
  public void fromCursor(final Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex(ObservationTable.Columns._ID));
    bssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.BSSID));
    capability = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.CAPABILITY));
    frequency = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.FREQUENCY));
    level = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.LEVEL));
    locationUuid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.LOCATION_ID));
    observationUuid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.OBSERVATION_ID));
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
  public Uri getTableUri() {
    return(ObservationTable.CONTENT_URI);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(final Long id) {
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

  public String getTimeStamp() {
    return timeStamp;
  }

  public String getLocationUuid() {
    return locationUuid;
  }

  public String getObservationUuid() {
    return observationUuid;
  }

  public String getSortieUuid() {
    return sortieUuid;
  }

  public boolean isUploadFlag() {
    return uploadFlag;
  }

  public void setUploadFlag() {
    uploadFlag = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ObservationModel that = (ObservationModel) o;

    if (frequency != that.frequency) return false;
    if (level != that.level) return false;
    if (timeStampMs != that.timeStampMs) return false;
    if (uploadFlag != that.uploadFlag) return false;
    if (!bssid.equals(that.bssid)) return false;
    if (!capability.equals(that.capability)) return false;
    if (!locationUuid.equals(that.locationUuid)) return false;
    if (!observationUuid.equals(that.observationUuid)) return false;
    if (!sortieUuid.equals(that.sortieUuid)) return false;
    if (!ssid.equals(that.ssid)) return false;
    if (!timeStamp.equals(that.timeStamp)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = ssid.hashCode();
    result = 31 * result + bssid.hashCode();
    result = 31 * result + capability.hashCode();
    result = 31 * result + locationUuid.hashCode();
    result = 31 * result + observationUuid.hashCode();
    result = 31 * result + sortieUuid.hashCode();
    result = 31 * result + (uploadFlag ? 1 : 0);
    result = 31 * result + timeStamp.hashCode();
    result = 31 * result + (int) (timeStampMs ^ (timeStampMs >>> 32));
    result = 31 * result + frequency;
    result = 31 * result + level;
    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */