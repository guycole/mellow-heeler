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

/**
 * @author gsc
 */
public class ObservationModel implements DataBaseModelIf {

  private Long id;
  private String ssid;
  private String bssid;
  private String capability;
  private String taskId;

  private boolean uploadFlag;

  private int frequency;
  private int level;

  private long timeStamp;

  private float accuracy;

  private double altitude;
  private double latitude;
  private double longitude;

  @Override
  public void setDefault(Context context) {
    ssid = "unknown";
    bssid = "unknown";
    capability = "unknown";
    taskId = Personality.getCurrentTask().getTaskId().toString();

    uploadFlag = false;

    frequency = 0;
    level = 0;

    timeStamp = TimeUtility.timeMillis();

    accuracy = -1.0f;

    altitude = 0.0;
    latitude = 0.0;
    longitude = 0.0;
  }

  public void setLocation(Location arg) {
    accuracy = arg.getAccuracy();
    altitude = arg.getAltitude();
    latitude = arg.getLatitude();
    longitude = arg.getLongitude();
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

    cv.put(ObservationTable.Columns.ACCURACY, accuracy);
    cv.put(ObservationTable.Columns.ALTITUDE, altitude);
    cv.put(ObservationTable.Columns.BSSID, bssid);
    cv.put(ObservationTable.Columns.CAPABILITY, capability);
    cv.put(ObservationTable.Columns.FREQUENCY, frequency);
    cv.put(ObservationTable.Columns.LATITUDE, latitude);
    cv.put(ObservationTable.Columns.LEVEL, level);
    cv.put(ObservationTable.Columns.LONGITUDE, longitude);
    cv.put(ObservationTable.Columns.SSID, ssid);
    cv.put(ObservationTable.Columns.TASK_ID, taskId);
    cv.put(ObservationTable.Columns.TIME_STAMP, timeStamp);

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
    accuracy = cursor.getFloat(cursor.getColumnIndex(ObservationTable.Columns.ACCURACY));
    altitude = cursor.getDouble(cursor.getColumnIndex(ObservationTable.Columns.ALTITUDE));
    bssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.BSSID));
    capability = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.CAPABILITY));
    frequency = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.FREQUENCY));
    latitude = cursor.getDouble(cursor.getColumnIndex(ObservationTable.Columns.LATITUDE));
    level = cursor.getInt(cursor.getColumnIndex(ObservationTable.Columns.LEVEL));
    longitude = cursor.getDouble(cursor.getColumnIndex(ObservationTable.Columns.LONGITUDE));
    ssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.SSID));
    taskId = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.TASK_ID));
    timeStamp = cursor.getLong(cursor.getColumnIndex(ObservationTable.Columns.TIME_STAMP));

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ObservationModel that = (ObservationModel) o;

    if (Float.compare(that.accuracy, accuracy) != 0) return false;
    if (Double.compare(that.altitude, altitude) != 0) return false;
    if (frequency != that.frequency) return false;
    if (Double.compare(that.latitude, latitude) != 0) return false;
    if (level != that.level) return false;
    if (Double.compare(that.longitude, longitude) != 0) return false;
    if (timeStamp != that.timeStamp) return false;
    if (uploadFlag != that.uploadFlag) return false;
    if (!bssid.equals(that.bssid)) return false;
    if (!capability.equals(that.capability)) return false;
    if (!ssid.equals(that.ssid)) return false;
    if (!taskId.equals(that.taskId)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = ssid.hashCode();
    result = 31 * result + bssid.hashCode();
    result = 31 * result + capability.hashCode();
    result = 31 * result + taskId.hashCode();
    result = 31 * result + (uploadFlag ? 1 : 0);
    result = 31 * result + frequency;
    result = 31 * result + level;
    result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
    result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
    temp = Double.doubleToLongBits(altitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(latitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */