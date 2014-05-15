package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.TimeWrapper;

/**
 * @author gsc
 */
public class LocationModel implements DataBaseModelIf {

  private Long id;
  private String taskId;

  private boolean uploadFlag;

  private String timeStamp;
  private long timeStampMs;

  private float accuracy;

  private double altitude;
  private double latitude;
  private double longitude;

  @Override
  public void setDefault(Context context) {
    taskId = Personality.getCurrentTask().getTaskId().toString();

    uploadFlag = false;

    timeStampMs = TimeUtility.timeMillis();
    TimeWrapper timeWrapper = new TimeWrapper(timeStampMs);
    timeStamp = timeWrapper.dateTimeFormat();

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

    timeStampMs = arg.getTime();
    TimeWrapper timeWrapper = new TimeWrapper(timeStampMs);
    timeStamp = timeWrapper.dateTimeFormat();
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
    cv.put(LocationTable.Columns.TASK_ID, taskId);

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
    taskId = cursor.getString(cursor.getColumnIndex(LocationTable.Columns.TASK_ID));
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

}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */