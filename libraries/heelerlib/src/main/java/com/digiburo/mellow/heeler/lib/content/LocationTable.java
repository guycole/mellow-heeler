package com.digiburo.mellow.heeler.lib.content;

import android.net.Uri;
import android.provider.BaseColumns;

import com.digiburo.mellow.heeler.lib.Constant;

import java.util.HashMap;
import java.util.Set;

/**
 * @author gsc
 */
public class LocationTable implements DataBaseTableIf {

  @Override
  public String getTableName() {
    return(TABLE_NAME);
  }

  @Override
  public String getDefaultSortOrder() {
    return(DEFAULT_SORT_ORDER);
  }

  @Override
  public String[] getDefaultProjection() {
    Set<String> keySet = LocationTable.PROJECTION_MAP.keySet();
    String[] result = (String[]) keySet.toArray(new String[keySet.size()]);
    return(result);
  }

  public static final class Columns implements BaseColumns {

    // column names
    public static final String ACCURACY = "accuracy";
    public static final String ALTITUDE = "altitude";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String TIME_STAMP = "time_stamp";
    public static final String TIME_STAMP_MS = "time_stamp_ms";
    public static final String UPLOAD_FLAG = "upload_flag";
    public static final String TASK_ID = "task_id";
  }

  //
  public static final String TABLE_NAME = "location";

  //
  public static final Uri CONTENT_URI = Uri.parse("content://" + Constant.AUTHORITY + "/" + TABLE_NAME);

  //
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digiburo." + TABLE_NAME;

  //
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digiburo." + TABLE_NAME;

  //
  public static final String DEFAULT_SORT_ORDER = "TIME_STAMP ASC";

  //
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
      + Columns._ID + " INTEGER PRIMARY KEY,"
      + Columns.ACCURACY + " REAL NOT NULL,"
      + Columns.ALTITUDE + " REAL NOT NULL,"
      + Columns.LATITUDE + " REAL NOT NULL,"
      + Columns.LONGITUDE + " REAL NOT NULL,"
      + Columns.TIME_STAMP + " INTEGER NOT NULL,"
      + Columns.TIME_STAMP_MS + " TEXT NOT NULL,"
      + Columns.UPLOAD_FLAG + " INTEGER NOT NULL,"
      + Columns.TASK_ID + " TEXT NOT NULL"
      + ");";

  //
  public static HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

  static {
    PROJECTION_MAP = new HashMap<String, String>();
    PROJECTION_MAP.put(LocationTable.Columns._ID, LocationTable.Columns._ID);
    PROJECTION_MAP.put(LocationTable.Columns.ACCURACY, LocationTable.Columns.ACCURACY);
    PROJECTION_MAP.put(LocationTable.Columns.ALTITUDE, LocationTable.Columns.ALTITUDE);
    PROJECTION_MAP.put(LocationTable.Columns.LATITUDE, LocationTable.Columns.LATITUDE);
    PROJECTION_MAP.put(LocationTable.Columns.LONGITUDE, LocationTable.Columns.LONGITUDE);
    PROJECTION_MAP.put(LocationTable.Columns.TIME_STAMP, LocationTable.Columns.TIME_STAMP);
    PROJECTION_MAP.put(LocationTable.Columns.TIME_STAMP_MS, LocationTable.Columns.TIME_STAMP_MS);
    PROJECTION_MAP.put(LocationTable.Columns.UPLOAD_FLAG, LocationTable.Columns.UPLOAD_FLAG);
    PROJECTION_MAP.put(LocationTable.Columns.TASK_ID, LocationTable.Columns.TASK_ID);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */
