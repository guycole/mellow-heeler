package net.braingang.heeler;

import android.net.Uri;
import android.provider.BaseColumns;

import net.braingang.heeler.Constant;

import java.util.HashMap;
import java.util.Set;

/**
 * device location history (within SQLite)
 * @author gsc
 */
public class LocationTable implements DataBaseTableIf {

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
    Set<String> keySet = LocationTable.PROJECTION_MAP.keySet();
    String[] result = (String[]) keySet.toArray(new String[keySet.size()]);
    return result;
  }

  public static final class Columns implements BaseColumns {

    // column names
    public static final String ACCURACY = "accuracy";
    public static final String ALTITUDE = "altitude";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String TIME_STAMP = "time_stamp";
    public static final String TIME_STAMP_MS = "time_stamp_ms";
    public static final String SPECIAL_FLAG = "special_flag";
    public static final String UPLOAD_FLAG = "upload_flag";
    public static final String LOCATION_ID = "location_id";
    public static final String SORTIE_ID = "sortie_id";
  }

  //
  public static final String TABLE_NAME = "location";

  //
  public static final Uri CONTENT_URI = Uri.parse("content://" + Constant.AUTHORITY + "/" + TABLE_NAME);

  //
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.braingang." + TABLE_NAME;

  //
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.braingang." + TABLE_NAME;

  //
  public static final String DEFAULT_SORT_ORDER = "TIME_STAMP_MS ASC";

  //
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
      + Columns._ID + " INTEGER PRIMARY KEY,"
      + Columns.ACCURACY + " REAL NOT NULL,"
      + Columns.ALTITUDE + " REAL NOT NULL,"
      + Columns.LATITUDE + " REAL NOT NULL,"
      + Columns.LONGITUDE + " REAL NOT NULL,"
      + Columns.TIME_STAMP + " INTEGER NOT NULL,"
      + Columns.TIME_STAMP_MS + " TEXT NOT NULL,"
      + Columns.SPECIAL_FLAG + " INTEGER NOT NULL,"
      + Columns.UPLOAD_FLAG + " INTEGER NOT NULL,"
      + Columns.LOCATION_ID + " TEXT NOT NULL,"
      + Columns.SORTIE_ID + " TEXT NOT NULL"
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
    PROJECTION_MAP.put(LocationTable.Columns.SPECIAL_FLAG, LocationTable.Columns.SPECIAL_FLAG);
    PROJECTION_MAP.put(LocationTable.Columns.UPLOAD_FLAG, LocationTable.Columns.UPLOAD_FLAG);
    PROJECTION_MAP.put(LocationTable.Columns.LOCATION_ID, LocationTable.Columns.LOCATION_ID);
    PROJECTION_MAP.put(LocationTable.Columns.SORTIE_ID, LocationTable.Columns.SORTIE_ID);
  }
}