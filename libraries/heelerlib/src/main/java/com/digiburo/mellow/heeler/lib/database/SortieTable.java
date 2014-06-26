package com.digiburo.mellow.heeler.lib.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.digiburo.mellow.heeler.lib.Constant;

import java.util.HashMap;
import java.util.Set;

/**
 * sortie history (within SQLite)
 * @author gsc
 */
public class SortieTable implements DataBaseTableIf {

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public String getDefaultSortOrder() {
    return DEFAULT_SORT_ORDER;
  }

  @Override
  public String[] getDefaultProjection() {
    Set<String> keySet = SortieTable.PROJECTION_MAP.keySet();
    String[] result = (String[]) keySet.toArray(new String[keySet.size()]);
    return result;
  }

  public static final class Columns implements BaseColumns {

    // column names
    public static final String TIME_STAMP = "time_stamp";
    public static final String TIME_STAMP_MS = "time_stamp_ms";
    public static final String UPLOAD_FLAG = "upload_flag";
    public static final String SORTIE_NAME = "sortie_name";
    public static final String SORTIE_ID = "sortie_id";
  }

  //
  public static final String TABLE_NAME = "sortie";

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
      + Columns.TIME_STAMP + " INTEGER NOT NULL,"
      + Columns.TIME_STAMP_MS + " TEXT NOT NULL,"
      + Columns.UPLOAD_FLAG + " INTEGER NOT NULL,"
      + Columns.SORTIE_NAME + " TEXT NOT NULL,"
      + Columns.SORTIE_ID + " TEXT NOT NULL"
      + ");";

  //
  public static HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

  static {
    PROJECTION_MAP = new HashMap<String, String>();
    PROJECTION_MAP.put(SortieTable.Columns._ID, SortieTable.Columns._ID);
    PROJECTION_MAP.put(SortieTable.Columns.TIME_STAMP, SortieTable.Columns.TIME_STAMP);
    PROJECTION_MAP.put(SortieTable.Columns.TIME_STAMP_MS, SortieTable.Columns.TIME_STAMP_MS);
    PROJECTION_MAP.put(SortieTable.Columns.UPLOAD_FLAG, SortieTable.Columns.UPLOAD_FLAG);
    PROJECTION_MAP.put(SortieTable.Columns.SORTIE_NAME, SortieTable.Columns.SORTIE_NAME);
    PROJECTION_MAP.put(SortieTable.Columns.SORTIE_ID, SortieTable.Columns.SORTIE_ID);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
