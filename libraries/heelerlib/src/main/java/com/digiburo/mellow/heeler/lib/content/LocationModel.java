package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * device location history
 * @author gsc
 */
public class LocationModel implements DataBaseModelIf {
  private Long id;
  private boolean uploadFlag;

  private String locationUuid;
  private String sortieUuid;

  private String timeStamp;
  private long timeStampMs;

  private double accuracy;
  private double altitude;
  private double latitude;
  private double longitude;

  @Override
  public void setDefault() {
    id = 0L;
    uploadFlag = false;

    locationUuid = UUID.randomUUID().toString();
    sortieUuid = UUID.randomUUID().toString();

    Time timeNow = TimeUtility.timeNow();
    timeStamp = timeNow.format3339(false);
    timeStampMs = timeNow.toMillis(Constant.IGNORE_DST);

    accuracy = -1.0f;
    altitude = 0.0;
    latitude = 0.0;
    longitude = 0.0;
  }

  @Override
  public ContentValues toContentValues() {
    ContentValues cv = new ContentValues();

    cv.put(LocationTable.Columns.ACCURACY, accuracy);
    cv.put(LocationTable.Columns.ALTITUDE, altitude);
    cv.put(LocationTable.Columns.LATITUDE, latitude);
    cv.put(LocationTable.Columns.LONGITUDE, longitude);

    cv.put(LocationTable.Columns.TIME_STAMP, timeStamp);
    cv.put(LocationTable.Columns.TIME_STAMP_MS, timeStampMs);
    cv.put(LocationTable.Columns.LOCATION_ID, locationUuid);
    cv.put(LocationTable.Columns.SORTIE_ID, sortieUuid);

    if (uploadFlag) {
      cv.put(LocationTable.Columns.UPLOAD_FLAG, Constant.SQL_TRUE);
    } else {
      cv.put(LocationTable.Columns.UPLOAD_FLAG, Constant.SQL_FALSE);
    }

    return(cv);
  }

  @Override
  public void fromCursor(Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex(LocationTable.Columns._ID));
    accuracy = cursor.getDouble(cursor.getColumnIndex(LocationTable.Columns.ACCURACY));
    altitude = cursor.getDouble(cursor.getColumnIndex(LocationTable.Columns.ALTITUDE));
    latitude = cursor.getDouble(cursor.getColumnIndex(LocationTable.Columns.LATITUDE));
    longitude = cursor.getDouble(cursor.getColumnIndex(LocationTable.Columns.LONGITUDE));
    locationUuid = cursor.getString(cursor.getColumnIndex(LocationTable.Columns.LOCATION_ID));
    sortieUuid = cursor.getString(cursor.getColumnIndex(LocationTable.Columns.SORTIE_ID));
    timeStamp = cursor.getString(cursor.getColumnIndex(LocationTable.Columns.TIME_STAMP));
    timeStampMs = cursor.getLong(cursor.getColumnIndex(LocationTable.Columns.TIME_STAMP_MS));

    int temp = cursor.getInt(cursor.getColumnIndex(LocationTable.Columns.UPLOAD_FLAG));
    if (temp == Constant.SQL_TRUE) {
      uploadFlag = true;
    } else {
      uploadFlag = false;
    }
  }

  @Override
  public String getTableName() {
    return(LocationTable.TABLE_NAME);
  }

  @Override
  public Uri getTableUri() {
    return(LocationTable.CONTENT_URI);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /**
   *
   * @param arg
   * @param sortieId
   */
  public void setLocation(Location arg, String sortieId) {
    accuracy = arg.getAccuracy();
    altitude = arg.getAltitude();
    latitude = arg.getLatitude();
    longitude = arg.getLongitude();

    Time time = new Time("UTC");
    time.set(arg.getTime());
    timeStamp = time.format3339(false);

    timeStampMs = arg.getTime();

    sortieUuid = sortieId;
  }

  public String getLocationUuid() {
    return locationUuid;
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

  public String getTimeStamp() {
    return timeStamp;
  }

  public long getTimeStampMs() {
    return timeStampMs;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public Double getAltitude() {
    return altitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LocationModel that = (LocationModel) o;

    if (Double.compare(that.accuracy, accuracy) != 0) return false;
    if (Double.compare(that.altitude, altitude) != 0) return false;
    if (Double.compare(that.latitude, latitude) != 0) return false;
    if (Double.compare(that.longitude, longitude) != 0) return false;
    if (timeStampMs != that.timeStampMs) return false;
    if (uploadFlag != that.uploadFlag) return false;
    if (!locationUuid.equals(that.locationUuid)) return false;
    if (!sortieUuid.equals(that.sortieUuid)) return false;
    if (!timeStamp.equals(that.timeStamp)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (uploadFlag ? 1 : 0);
    result = 31 * result + locationUuid.hashCode();
    result = 31 * result + sortieUuid.hashCode();
    result = 31 * result + timeStamp.hashCode();
    result = 31 * result + (int) (timeStampMs ^ (timeStampMs >>> 32));
    temp = Double.doubleToLongBits(accuracy);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
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