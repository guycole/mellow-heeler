package com.digiburo.mellow.heeler.lib.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.digiburo.mellow.heeler.lib.Constant;

import java.util.HashMap;
import java.util.Set;

/**
 * detection history (within SQLite)
 * @author gsc
 */
public class HotTable implements DataBaseTableIf {

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
    Set<String> keySet = HotTable.PROJECTION_MAP.keySet();
    String[] result = (String[]) keySet.toArray(new String[keySet.size()]);
    return(result);
  }

  public static final class Columns implements BaseColumns {

    // column names
    public static final String SSID = "ssid";
    public static final String BSSID = "bssid";
  }

  //
  public static final String TABLE_NAME = "hot";

  //
  public static final Uri CONTENT_URI = Uri.parse("content://" + Constant.AUTHORITY + "/" + TABLE_NAME);

  //
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.digiburo." + TABLE_NAME;

  //
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.digiburo." + TABLE_NAME;

  //
  public static final String DEFAULT_SORT_ORDER = "SSID ASC";

  //
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
      + Columns._ID + " INTEGER PRIMARY KEY,"
      + Columns.SSID + " TEXT NOT NULL,"
      + Columns.BSSID + " TEXT NOT NULL"
      + ");";

  //
  public static HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

  static {
    PROJECTION_MAP = new HashMap<String, String>();
    PROJECTION_MAP.put(HotTable.Columns._ID, HotTable.Columns._ID);
    PROJECTION_MAP.put(HotTable.Columns.SSID, HotTable.Columns.SSID);
    PROJECTION_MAP.put(HotTable.Columns.BSSID, HotTable.Columns.BSSID);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 22, 2014 by gsc
 */
