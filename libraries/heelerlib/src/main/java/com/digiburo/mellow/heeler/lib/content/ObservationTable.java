package com.digiburo.mellow.heeler.lib.content;

import android.net.Uri;
import android.provider.BaseColumns;

import com.digiburo.mellow.heeler.lib.Constant;

import java.util.HashMap;
import java.util.Set;

/**
 * detection history (within SQLite)
 * @author gsc
 */
public class ObservationTable implements DataBaseTableIf {

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
    Set<String> keySet = ObservationTable.PROJECTION_MAP.keySet();
    String[] result = (String[]) keySet.toArray(new String[keySet.size()]);
    return(result);
  }

  public static final class Columns implements BaseColumns {

    // column names
    public static final String SSID = "ssid";
    public static final String BSSID = "bssid";
    public static final String CAPABILITY = "capability";
    public static final String FREQUENCY = "frequency";
    public static final String LEVEL = "level";

    public static final String TIME_STAMP = "time_stamp";
    public static final String TIME_STAMP_MS = "time_stamp_ms";
    public static final String UPLOAD_FLAG = "upload_flag";
    public static final String LOCATION_ID = "location_id";
    public static final String SORTIE_ID = "sortie_id";
  }

  //
  public static final String TABLE_NAME = "observation";

  //
  public static final Uri CONTENT_URI = Uri.parse("content://" + Constant.AUTHORITY + "/" + TABLE_NAME);

  //
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digiburo." + TABLE_NAME;

  //
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digiburo." + TABLE_NAME;

  //
  public static final String DEFAULT_SORT_ORDER = "TIME_STAMP_MS ASC";

  //
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
      + Columns._ID + " INTEGER PRIMARY KEY,"
      + Columns.SSID + " TEXT NOT NULL,"
      + Columns.BSSID + " TEXT NOT NULL,"
      + Columns.CAPABILITY + " TEXT NOT NULL,"
      + Columns.FREQUENCY + " INTEGER NOT NULL,"
      + Columns.LEVEL + " INTEGER NOT NULL,"
      + Columns.TIME_STAMP + " TEXT NOT NULL,"
      + Columns.TIME_STAMP_MS + " INTEGER NOT NULL,"
      + Columns.UPLOAD_FLAG + " INTEGER NOT NULL,"
      + Columns.LOCATION_ID + " TEXT NOT NULL,"
      + Columns.SORTIE_ID + " TEXT NOT NULL"
      + ");";

  //
  public static HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

  static {
    PROJECTION_MAP = new HashMap<String, String>();
    PROJECTION_MAP.put(ObservationTable.Columns._ID, ObservationTable.Columns._ID);
    PROJECTION_MAP.put(ObservationTable.Columns.SSID, ObservationTable.Columns.SSID);
    PROJECTION_MAP.put(ObservationTable.Columns.BSSID, ObservationTable.Columns.BSSID);
    PROJECTION_MAP.put(ObservationTable.Columns.CAPABILITY, ObservationTable.Columns.CAPABILITY);
    PROJECTION_MAP.put(ObservationTable.Columns.FREQUENCY, ObservationTable.Columns.FREQUENCY);
    PROJECTION_MAP.put(ObservationTable.Columns.LEVEL, ObservationTable.Columns.LEVEL);
    PROJECTION_MAP.put(ObservationTable.Columns.TIME_STAMP, ObservationTable.Columns.TIME_STAMP);
    PROJECTION_MAP.put(ObservationTable.Columns.TIME_STAMP_MS, ObservationTable.Columns.TIME_STAMP_MS);
    PROJECTION_MAP.put(ObservationTable.Columns.UPLOAD_FLAG, ObservationTable.Columns.UPLOAD_FLAG);
    PROJECTION_MAP.put(ObservationTable.Columns.LOCATION_ID, ObservationTable.Columns.LOCATION_ID);
    PROJECTION_MAP.put(ObservationTable.Columns.SORTIE_ID, ObservationTable.Columns.SORTIE_ID);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */
