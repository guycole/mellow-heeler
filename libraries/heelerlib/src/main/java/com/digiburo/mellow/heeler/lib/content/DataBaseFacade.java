package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.utility.StringList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * database convenience methods
 * @author gsc
 */
public class DataBaseFacade {
  private static final Logger LOG = LoggerFactory.getLogger(DataBaseFacade.class);

  private final String dataBaseFileName;

  /**
   *
   * @param context
   */
  public DataBaseFacade(Context context) {
    if (Personality.isInternalDataBaseFileSystem()) {
      dataBaseFileName = DataBaseHelper.DATABASE_FILE_NAME;
    } else {
      dataBaseFileName = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + DataBaseHelper.DATABASE_FILE_NAME;
    }
  }

  /**
   * insert a fresh row
   * @param candidate
   * @param context
   * @return
   */
  public Long insert(DataBaseModelIf model, Context context) {
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
   *
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteLocation(long rowKey, Context context) {
    String where = LocationTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {Long.toString(rowKey)};
    return simpleDelete(LocationTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * select location by row id
   * @param target row id
   * @param context
   * @return selected model
   */
  public LocationModel selectLocation(Long target, Context context) {
    LocationModel model = new LocationModel();
    LocationTable table = new LocationTable();

    String selection = LocationTable.Columns._ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * select all locations
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public LocationModelList selectAllLocations(boolean allRows, Context context) {
    LocationModelList results = new LocationModelList();

    LocationTable table = new LocationTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    if (!allRows) {
      selection = LocationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{Constant.SQL_FALSE.toString()};
    }

    Cursor cursor = null;
    SQLiteDatabase sqlDb = null;

    try {
      sqlDb = getReadableDataBase(context);
      cursor = sqlDb.query(LocationTable.TABLE_NAME, projection, selection, selectionArgs, null, null, LocationTable.DEFAULT_SORT_ORDER);
      if (cursor.moveToFirst()) {
        do {
          LocationModel locationModel = new LocationModel();
          locationModel.fromCursor(cursor);
          results.add(locationModel);
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
   * update existing location
   * @param model
   * @param context
   * @return
   */
  public int updateLocation(LocationModel model, Context context) {
    String where = LocationTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   *
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteObservation(long rowKey, Context context) {
    String where = ObservationTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {Long.toString(rowKey)};
    return simpleDelete(ObservationTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * select all observations
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public ObservationModelList selectAllObservations(boolean allRows, Context context) {
    ObservationModelList results = new ObservationModelList();

    ObservationTable table = new ObservationTable();
    String[] projection = table.getDefaultProjection();

    String selection = null;
    String[] selectionArgs = null;

    if (!allRows) {
      selection = ObservationTable.Columns.UPLOAD_FLAG + "=?";
      selectionArgs = new String[]{Constant.SQL_FALSE.toString()};
    }

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
   * select observation by row id
   * @param target row id
   * @param context
   * @return selected observation
   */
  public ObservationModel selectObservation(Long target, Context context) {
    ObservationModel model = new ObservationModel();
    ObservationTable table = new ObservationTable();

    String selection = ObservationTable.Columns._ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * update existing observation
   * @param model
   * @param context
   * @return
   */
  public int updateObservation(ObservationModel model, Context context) {
    String where = ObservationTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   *
   * @param rowKey
   * @param context
   * @return
   */
  public int deleteSortie(long rowKey, Context context) {
    String where = SortieTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {Long.toString(rowKey)};
    return simpleDelete(SortieTable.TABLE_NAME, where, whereArgs, context);
  }

  /**
   * select all sorties
   * @param allRows true return all rows else only uploadFlag false
   * @param context
   * @return
   */
  public SortieModelList selectAllSorties(boolean allRows, Context context) {
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
  public SortieModel selectSortie(Long target, Context context) {
    SortieModel model = new SortieModel();
    SortieTable table = new SortieTable();

    String selection = SortieTable.Columns._ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * select sortie by UUID
   * @param target UUID
   * @param context
   * @return selected model
   */
  public SortieModel selectSortie(String target, Context context) {
    SortieModel model = new SortieModel();
    SortieTable table = new SortieTable();

    String selection = SortieTable.Columns.SORTIE_ID + "=?";
    String[] selectionArgs = new String[] {target.toString()};

    simpleSelect(selection, selectionArgs, table, model, context);

    return model;
  }

  /**
   * update existing sortie
    * @param model
   * @param context
   * @return
   */
  public int updateSortie(SortieModel model, Context context) {
    String where = SortieTable.Columns._ID + "=?";
    String[] whereArgs = new String[] {model.getId().toString()};
    return simpleUpdate(model, where, whereArgs, context);
  }

  /**
   * simple delete
   * @param tableName
   * @param where
   * @param whereArgs
   * @param context
   * @return
   */
  private int simpleDelete(String tableName, String where, String[] whereArgs, Context context) {
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
  private boolean simpleSelect(String selection, String[] selectionArgs, DataBaseTableIf table, DataBaseModelIf model, Context context) {
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
  private int simpleUpdate(DataBaseModelIf model, String where, String[] whereArgs, Context context) {
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

    ///////////////////

  /**
   * mark locations as uploaded
   * @param target
   * @param context
   */
  public void setLocationUpload(Long target, Context context) {
    LocationModel model = new LocationModel();
    model.setDefault();

    SQLiteDatabase sqlDb = getWritableDataBase(context);

    String selection = "_id=?";
    String[] selectionArgs = new String[] {Long.toString(target)};

    Cursor cursor = sqlDb.query(LocationTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
    if (cursor.moveToFirst()) {
      model.fromCursor(cursor);
      model.setUploadFlag();
    }

    cursor.close();

    if (model.getId().longValue() > 0) {
      LOG.debug("location selected:" + target);
      sqlDb.update(LocationTable.TABLE_NAME, model.toContentValues(), selection, selectionArgs);
    } else {
      LOG.debug("location not found:" + target);
    }

    sqlDb.close();
  }


  /**
   * discover sorties
   * @param context
   * @return list of sorties w/location rows to upload
   */
  public StringList selectLocationSorties(Context context) {
    StringList results = new StringList();

    SQLiteDatabase sqlDb = getReadableDataBase(context);

    String[] projection = new String[]{LocationTable.Columns.SORTIE_ID};

    String selection = LocationTable.Columns.UPLOAD_FLAG + "=?";
    String[] selectionArgs = new String[]{Constant.SQL_FALSE.toString()};

    //select distinct
    Cursor cursor = sqlDb.query(true, LocationTable.TABLE_NAME, projection, selection, selectionArgs, LocationTable.Columns.SORTIE_ID, null, null, null, null);
    if (cursor.moveToFirst()) {
      do {
        results.add(cursor.getString(0));
      } while (cursor.moveToNext());
    }

    cursor.close();
    sqlDb.close();

    return results;
  }

  /**
   * discover sorties
   * @param context
   * @return list of sorties w/observation rows to upload
   */
  public StringList selectObservationSorties(Context context) {
    StringList results = new StringList();

    SQLiteDatabase sqlDb = getReadableDataBase(context);

    String[] projection = new String[]{ObservationTable.Columns.SORTIE_ID};

    String selection = ObservationTable.Columns.UPLOAD_FLAG + "=?";
    String[] selectionArgs = new String[]{Constant.SQL_FALSE.toString()};

    //select distinct
    Cursor cursor = sqlDb.query(true, ObservationTable.TABLE_NAME, projection, selection, selectionArgs, ObservationTable.Columns.SORTIE_ID, null, null, null, null);
    if (cursor.moveToFirst()) {
      do {
        results.add(cursor.getString(0));
      } while (cursor.moveToNext());
    }

    cursor.close();
    sqlDb.close();

    return results;
  }

  /**
   * select rows by sortie
   * @param context
   * @param sortie
   * @return
   */
  public List<LocationModel> selectLocationsBySortie(Context context, String sortie) {
    ArrayList<LocationModel> results = new ArrayList<LocationModel>();

    SQLiteDatabase sqlDb = getReadableDataBase(context);

    String selection = LocationTable.Columns.UPLOAD_FLAG + "=? and " + LocationTable.Columns.SORTIE_ID + "=?";
    String[] selectionArgs = new String[]{Constant.SQL_FALSE.toString(), sortie};

    Cursor cursor = context.getContentResolver().query(LocationTable.CONTENT_URI, null, selection, selectionArgs, LocationTable.DEFAULT_SORT_ORDER);
    if (cursor.moveToFirst()) {
      do {
        LocationModel locationModel = new LocationModel();
        locationModel.fromCursor(cursor);
        results.add(locationModel);
      } while(cursor.moveToNext());
    }

    cursor.close();
    sqlDb.close();

    return results;
  }

  /**
   * select rows by sortie
   * @param context
   * @param sortie
   * @return
   */
  public List<ObservationModel> selectObservationsBySortie(Context context, String sortie) {
    ArrayList<ObservationModel> results = new ArrayList<ObservationModel>();

    SQLiteDatabase sqlDb = getReadableDataBase(context);

    String selection = ObservationTable.Columns.UPLOAD_FLAG + "=? and " + ObservationTable.Columns.SORTIE_ID + "=?";
    String[] selectionArgs = new String[]{Constant.SQL_FALSE.toString(), sortie};

    Cursor cursor = context.getContentResolver().query(ObservationTable.CONTENT_URI, null, selection, selectionArgs, ObservationTable.DEFAULT_SORT_ORDER);
    if (cursor.moveToFirst()) {
      do {
        ObservationModel observationModel = new ObservationModel();
        observationModel.fromCursor(cursor);
        results.add(observationModel);
      } while(cursor.moveToNext());
    }

    cursor.close();
    sqlDb.close();

    return results;
  }

  public void deleteLocations(Context context, List<LocationModel> locationModelList) {
    SQLiteDatabase sqlDb = getReadableDataBase(context);

    for(LocationModel locationModel:locationModelList) {
      String whereClause = "_id=?";
      String[] whereArgs = new String[]{locationModel.getId().toString()};
      sqlDb.delete(LocationTable.TABLE_NAME, whereClause, whereArgs);
    }

    sqlDb.close();
  }

  private SQLiteDatabase getReadableDataBase(Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(context, dataBaseFileName);
    return dataBaseHelper.getReadableDatabase();
  }

  private SQLiteDatabase getWritableDataBase(Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(context, dataBaseFileName);
    return dataBaseHelper.getWritableDatabase();
  }
}
