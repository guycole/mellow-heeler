package com.digiburo.mellow.heeler.lib.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * database convenience methods
 * @author gsc
 */
public class DataBaseFacade {
  private static final Logger LOG = LoggerFactory.getLogger(DataBaseFacade.class);

  private final String dataBaseFileName;

  /**
   * @param context
   */
  public DataBaseFacade(final Context context) {
    dataBaseFileName = DataBaseUtility.dataBaseFileName(Personality.isInternalDataBaseFileSystem(), context);
  }

  /**
   * insert a fresh row
   *
   * @param candidate
   * @param context
   * @return
   */
  public Long insert(final DataBaseModelIf model, final Context context) {
    Long rowId = 0L;
    SQLiteDatabase sqlDb = getWritableDataBase(context);

    try {
      sqlDb.beginTransaction();
      rowId = sqlDb.insert(model.getTableName(), null, model.toContentValues());
      model.setId(rowId);
      sqlDb.setTransactionSuccessful();
    } finally {
      sqlDb.endTransaction();
      sqlDb.close();
    }

    return rowId;
  }

  /**
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteHot(long rowKey, final Context context) {
    String where = HotTable.Columns._ID + "=?";
    String[] whereArgs = new String[]{Long.toString(rowKey)};
    return simpleDelete(HotTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * @param context
   * @return
   */
  public HotModelList selectAllHot(final Context context) {
    HotModelList results = new HotModelList();

    HotTable table = new HotTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(HotTable.TABLE_NAME, projection, selection, selectionArgs, null, null, HotTable.DEFAULT_SORT_ORDER);
      if (cursor.moveToFirst()) {
        do {
          HotModel hotModel = new HotModel();
          hotModel.fromCursor(cursor);
          results.add(hotModel);
        } while (cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  /**
   * @param target
   * @param context
   * @return
   */
  public HotModel selectHot(final Long target, final Context context) {
    HotModel model = new HotModel();
    HotTable table = new HotTable();

    String selection = HotTable.Columns._ID + "=?";
    String[] selectionArgs = new String[]{target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   *
   * @param target
   * @param context
   * @return
   */
  public HotModel selectHot(final String target, final Context context) {
    HotModel model = new HotModel();
    HotTable table = new HotTable();

    String selection = HotTable.Columns.BSSID + "=?";
    String[] selectionArgs = new String[]{target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * @param model
   * @param context
   * @return
   */
  public int updateHot(final HotModel model, final Context context) {
    String where = HotTable.Columns._ID + "=?";
    String[] whereArgs = new String[]{model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteLocation(long rowKey, final Context context) {
    String where = LocationTable.Columns._ID + "=?";
    String[] whereArgs = new String[]{Long.toString(rowKey)};
    return simpleDelete(LocationTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * select location by row id
   *
   * @param target  row id
   * @param context
   * @return selected model
   */
  public LocationModel selectLocation(final Long target, final Context context) {
    LocationModel model = new LocationModel();
    LocationTable table = new LocationTable();

    String selection = LocationTable.Columns._ID + "=?";
    String[] selectionArgs = new String[]{target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * @param uuid
   * @param context
   * @return selected model
   */
  public LocationModel selectLocation(final String uuid, final Context context) {
    LocationModel model = new LocationModel();
    LocationTable table = new LocationTable();

    String selection = LocationTable.Columns.LOCATION_ID + "=?";
    String[] selectionArgs = new String[]{uuid};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * return row population
   *
   * @param allRows true return all rows else only uploadFlag false
   * @param sortieUuid
   * @param context
   * @return
   */
  public long countLocationRows(boolean allRows, final String sortieUuid, final Context context) {
    long population = 0;
    SQLiteDatabase sqlDb = null;

    String selection = null;
    String[] selectionArgs = null;

    if (allRows) {
      selection = LocationTable.Columns.SORTIE_ID + "=?";
      selectionArgs = new String[]{sortieUuid};
    } else {
      selection = LocationTable.Columns.SORTIE_ID + "=? and " + LocationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{sortieUuid, Constant.SQL_FALSE.toString()};
    }

    try {
      sqlDb = getReadableDataBase(context);
      DatabaseUtils databaseUtils = new DatabaseUtils();
      population = databaseUtils.queryNumEntries(sqlDb, LocationTable.TABLE_NAME, selection, selectionArgs);
    } finally {
      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return population;
  }

  /**
   * select all locations
   *
   * @param allRows true return all rows else only uploadFlag false
   * @param sortieUuid
   * @param rowLimit
   * @param context
   * @return
   */
  public LocationModelList selectAllLocations(boolean allRows, final String sortieUuid, final int rowLimit, final Context context) {
    String rowLimitString = null;
    if (rowLimit > 0) {
      rowLimitString = Integer.toString(rowLimit);
    }

    LocationModelList results = new LocationModelList();

    LocationTable table = new LocationTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    if (allRows) {
      selection = LocationTable.Columns.SORTIE_ID + "=?";
      selectionArgs = new String[]{sortieUuid};
    } else {
      selection = LocationTable.Columns.SORTIE_ID + "=? and " + LocationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{sortieUuid, Constant.SQL_FALSE.toString()};
    }

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(LocationTable.TABLE_NAME, projection, selection, selectionArgs, null, null, LocationTable.DEFAULT_SORT_ORDER, rowLimitString);
      if (cursor.moveToFirst()) {
        do {
          LocationModel locationModel = new LocationModel();
          locationModel.fromCursor(cursor);
          results.add(locationModel);
        } while (cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  /**
   * update existing location
   *
   * @param model
   * @param context
   * @return
   */
  public int updateLocation(final LocationModel model, final Context context) {
    String where = LocationTable.Columns._ID + "=?";
    String[] whereArgs = new String[]{model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteObservation(long rowKey, final Context context) {
    String where = ObservationTable.Columns._ID + "=?";
    String[] whereArgs = new String[]{Long.toString(rowKey)};
    return simpleDelete(ObservationTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * mark location as special interest
   *
   * @param target  row key
   * @param context
   */
  public void setLocationSpecial(final Long target, final Context context) {
    LocationModel locationModel = selectLocation(target, context);
    if (locationModel.getId().longValue() == target.longValue()) {
      locationModel.setSpecialFlag();
      updateLocation(locationModel, context);
    }
  }

  /**
   * mark location as uploaded
   *
   * @param target  row key
   * @param context
   */
  public void setLocationUpload(final Long target, final Context context) {
    LocationModel locationModel = selectLocation(target, context);
    if (locationModel.getId().longValue() == target.longValue()) {
      locationModel.setUploadFlag();
      updateLocation(locationModel, context);
    }
  }

  /**
   * return row population
   *
   * @param allRows true return all rows else only uploadFlag false
   * @param sortieUuid
   * @param context
   * @return
   */
  public long countObservationRows(boolean allRows, final String sortieUuid, final Context context) {
    long population = 0;
    SQLiteDatabase sqlDb = null;

    String selection = null;
    String[] selectionArgs = null;

    if (allRows) {
      selection = ObservationTable.Columns.SORTIE_ID + "=?";
      selectionArgs = new String[]{sortieUuid};
    } else {
      selection = ObservationTable.Columns.SORTIE_ID + "=? and " + ObservationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{sortieUuid, Constant.SQL_FALSE.toString()};
    }

    try {
      sqlDb = getReadableDataBase(context);
      DatabaseUtils databaseUtils = new DatabaseUtils();
      population = databaseUtils.queryNumEntries(sqlDb, ObservationTable.TABLE_NAME, selection, selectionArgs);
    } finally {
      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return population;
  }

  /**
   * return a list of SSID which have unique BSSID
   * @param context
   * @return
   */
  public ObservationModelList selectDistinctBssid(final Context context) {
    ObservationModelList results = new ObservationModelList();

    ObservationTable table = new ObservationTable();
    String[] projection = table.getDefaultProjection();

    String having = null;
    String groupBy = ObservationTable.Columns.BSSID;
    String orderBy = ObservationTable.Columns.SSID;
    String limit = null;

    String selection = null;
    String[] selectionArgs = null;

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(true, ObservationTable.TABLE_NAME, projection, selection, selectionArgs, groupBy, having, orderBy, limit);
      if (cursor.moveToFirst()) {
        do {
          ObservationModel observationModel = new ObservationModel();
          observationModel.fromCursor(cursor);
          results.add(observationModel);
        } while(cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  public ObservationModelList selectBssidObservations(final String bssid, final Context context) {
    ObservationModelList results = new ObservationModelList();

    ObservationTable table = new ObservationTable();
    String[] projection = table.getDefaultProjection();

    String selection = ObservationTable.Columns.BSSID + "=?";
    String[] selectionArgs = new String[]{bssid};

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(ObservationTable.TABLE_NAME, projection, selection, selectionArgs, null, null, ObservationTable.DEFAULT_SORT_ORDER);
      if (cursor.moveToFirst()) {
        do {
          ObservationModel observationModel = new ObservationModel();
          observationModel.fromCursor(cursor);
          results.add(observationModel);
        } while(cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  /**
   * select all observations
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public ObservationModelList selectAllObservations(boolean allRows, final String sortieUuid, final int rowLimit, final Context context) {
    String rowLimitString = null;
    if (rowLimit > 0) {
      rowLimitString = Integer.toString(rowLimit);
    }

    ObservationModelList results = new ObservationModelList();

    ObservationTable table = new ObservationTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    if (allRows) {
      selection = ObservationTable.Columns.SORTIE_ID + "=?";
      selectionArgs = new String[]{sortieUuid};
    } else {
      selection = ObservationTable.Columns.SORTIE_ID + "=? and " + ObservationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{sortieUuid, Constant.SQL_FALSE.toString()};
    }

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(ObservationTable.TABLE_NAME, projection, selection, selectionArgs, null, null, ObservationTable.DEFAULT_SORT_ORDER, rowLimitString);
      if (cursor.moveToFirst()) {
        do {
          ObservationModel observationModel = new ObservationModel();
          observationModel.fromCursor(cursor);
          results.add(observationModel);
        } while(cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  /**
   * select observation by row id
   * @param target row id
   * @param context
   * @return selected observation
   */
  public ObservationModel selectObservation(final Long target, final Context context) {
    ObservationModel model = new ObservationModel();
    ObservationTable table = new ObservationTable();

    String selection = ObservationTable.Columns._ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  public ObservationModel selectObservation(final String uuid, final Context context) {
    ObservationModel model = new ObservationModel();
    ObservationTable table = new ObservationTable();

    String selection = ObservationTable.Columns.OBSERVATION_ID + "=?";
    String[] selectionArgs = new String[] {uuid};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * update existing observation
   * @param model
   * @param context
   * @return
   */
  public int updateObservation(final ObservationModel model, final Context context) {
    String where = ObservationTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   * mark observation as uploaded
   * @param target row key
   * @param context
   */
  public void setObservationUpload(final Long target, final Context context) {
    ObservationModel observationModel = selectObservation(target, context);
    if (observationModel.getId().longValue() == target.longValue()) {
      observationModel.setUploadFlag();
      updateObservation(observationModel, context);
    }
  }

  /**
   *
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteSortie(long rowKey, final Context context) {
    String where = SortieTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {Long.toString(rowKey)};
    return simpleDelete(SortieTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * return row population
   *
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public long countSortieRows(boolean allRows, final Context context) {
    long population = 0;
    SQLiteDatabase sqlDb = null;

    String selection = null;
    String[] selectionArgs = null;

    if (!allRows) {
      selection = SortieTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{Constant.SQL_FALSE.toString()};
    }

    try {
      sqlDb = getReadableDataBase(context);
      DatabaseUtils databaseUtils = new DatabaseUtils();
      population = databaseUtils.queryNumEntries(sqlDb, SortieTable.TABLE_NAME, selection, selectionArgs);
    } finally {
      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return population;
  }

  /**
   * select all sorties
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public SortieModelList selectAllSorties(boolean allRows, final Context context) {
    SortieModelList results = new SortieModelList();

    SortieTable table = new SortieTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    if (!allRows) {
      selection = SortieTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{Constant.SQL_FALSE.toString()};
    }

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(SortieTable.TABLE_NAME, projection, selection, selectionArgs, null, null, SortieTable.DEFAULT_SORT_ORDER);
      if (cursor.moveToFirst()) {
        do {
          SortieModel sortieModel = new SortieModel();
          sortieModel.fromCursor(cursor);
          results.add(sortieModel);
        } while(cursor.moveToNext());
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }

      if (sqlDb != null) {
        sqlDb.close();
      }
    }

    return results;
  }

  /**
   * select sortie by row id
   * @param target row id
   * @param context
   * @return selected model
   */
  public SortieModel selectSortie(final Long target, final Context context) {
    SortieModel model = new SortieModel();
    SortieTable table = new SortieTable();

    String selection = SortieTable.Columns._ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * select sortie by UUID
   * @param uuid UUID
   * @param context
   * @return selected model
   */
  public SortieModel selectSortie(final String uuid, final Context context) {
    SortieModel model = new SortieModel();
    SortieTable table = new SortieTable();

    String selection = SortieTable.Columns.SORTIE_ID + "=?";
    String[] selectionArgs = new String[] {uuid};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * update existing sortie
    * @param model
   * @param context
   * @return
   */
  public int updateSortie(final SortieModel model, final Context context) {
    String where = SortieTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   * mark sortie as uploaded
   * @param target row key
   * @param context
   */
  public void setSortieUpload(final Long target, final Context context) {
    SortieModel sortieModel = selectSortie(target, context);
    if (sortieModel.getId().longValue() == target.longValue()) {
      sortieModel.setUploadFlag();
      updateSortie(sortieModel, context);
    }
  }

  /**
   * simple delete
   * @param tableName
   * @param where
   * @param whereArgs
   * @param context
   * @return
   */
  private int simpleDelete(final String tableName, final String where, final String[] whereArgs, final Context context) {
    int count = 0;

    SQLiteDatabase sqlDb = getWritableDataBase(context);

    try {
      sqlDb.beginTransaction();
      count = sqlDb.delete(tableName, where, whereArgs);
      sqlDb.setTransactionSuccessful();
    } finally {
      sqlDb.endTransaction();
      sqlDb.close();
    }

    return count;
  }

  /**
   * select/return a single row
   * @param selection
   * @param selectionArgs
   * @param table
   * @param model updated selection
   * @param context
   * @return true success
   */
  private boolean simpleSelect(final String selection, final String[] selectionArgs, final DataBaseTableIf table, final DataBaseModelIf model, final Context context) {
    model.setDefault();

    String[] projection = table.getDefaultProjection();
    String sortOrder = table.getDefaultSortOrder();

    SQLiteDatabase sqlDb = getReadableDataBase(context);

    boolean status = false;
    Cursor cursor = null;

    try {
      cursor = sqlDb.query(model.getTableName(), projection, selection, selectionArgs, null, null, sortOrder);
      if (cursor.moveToFirst()) {
        model.fromCursor(cursor);
        status = true;
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

    return(status);
  }

  /**
   * simple update
   * @param model
   * @param where
   * @param whereArgs
   * @param context
   * @return
   */
  private int simpleUpdate(final DataBaseModelIf model, final String where, final String[] whereArgs, final Context context) {
    int count = 0;

    SQLiteDatabase sqlDb = getWritableDataBase(context);

    try {
      sqlDb.beginTransaction();
      count = sqlDb.update(model.getTableName(), model.toContentValues(), where, whereArgs);
      sqlDb.setTransactionSuccessful();
    } finally {
      sqlDb.endTransaction();
      sqlDb.close();
    }

    return count;
  }

  /**
   * delete uploaded items from specified table
   * @param tableName
   * @param columnName
   * @return
   */
  public int deleteUploaded(final String tableName, final String columnName, final Context context) {
    String where = columnName + "=?";
    String[] whereArgs = new String[] {Constant.SQL_TRUE.toString()};
    return simpleDelete(tableName, where, whereArgs, context);
  }

  /**
   * delete entire table
   * @param tableName
   * @param context
   * @return
   */
  public int deleteAll(final String tableName, final Context context) {
    return simpleDelete(tableName, null, null, context);
  }

  /**
   *
   * @param context
   * @return
   */
  private SQLiteDatabase getReadableDataBase(final Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(dataBaseFileName, context);
    return dataBaseHelper.getReadableDatabase();
  }

  /**
   *
   * @param context
   * @return
   */
  private SQLiteDatabase getWritableDataBase(final Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(dataBaseFileName, context);
    return dataBaseHelper.getWritableDatabase();
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */

