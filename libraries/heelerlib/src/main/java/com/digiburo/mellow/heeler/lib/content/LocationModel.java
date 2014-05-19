package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * device location history
 * @author gsc
 */
public class LocationModel implements DataBaseModelIf {
  private Long id;
  private String locationUuid;
  private String sortieUuid;

  private boolean uploadFlag;

  private String timeStamp;
  private long timeStampMs;

  private float accuracy;

  private double altitude;
  private double latitude;
  private double longitude;

  @Override
  public void setDefault(Context context) {
    locationUuid = UUID.randomUUID().toString();
    sortieUuid = Personality.getCurrentSortie().getSortieId().toString();

    uploadFlag = false;

    Time timeNow = TimeUtility.timeNow();
    timeStamp = timeNow.format3339(false);
    timeStampMs = timeNow.toMillis(Constant.IGNORE_DST);

    accuracy = -1.0f;

    altitude = 0.0;
    latitude = 0.0;
    longitude = 0.0;
  }

  @Override
  public ContentValues toContentValues(Context context) {
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
    accuracy = cursor.getFloat(cursor.getColumnIndex(LocationTable.Columns.ACCURACY));
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
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public void setLocation(Location arg) {
    accuracy = arg.getAccuracy();
    altitude = arg.getAltitude();
    latitude = arg.getLatitude();
    longitude = arg.getLongitude();

    Time time = new Time("UTC");
    time.set(arg.getTime());
    timeStamp = time.format3339(false);

    timeStampMs = arg.getTime();
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

  public float getAccuracy() {
    return accuracy;
  }

  public double getAltitude() {
    return altitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */