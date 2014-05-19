package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.StringList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gsc
 */
public class DataBaseFacade {
  private static final Logger LOG = LoggerFactory.getLogger(DataBaseFacade.class);

  /**
   * insert a new location
   * @param locationModel
   * @param context
   * @return
   */
  public Long newLocation(LocationModel locationModel, Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getWritableDatabase();
    ContentValues contentValues = locationModel.toContentValues(context);
    Long rowId = sqlDb.insert(LocationTable.TABLE_NAME, null, contentValues);
    LOG.debug("new location:" + rowId);
    sqlDb.close();
    return rowId;
  }

  /**
   * mark locations as uploaded
   * @param target
   * @param context
   */
  public void setLocationUpload(Long target, Context context) {
    LocationModel model = new LocationModel();
    model.setDefault(context);

    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getWritableDatabase();

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
      sqlDb.update(LocationTable.TABLE_NAME, model.toContentValues(context), selection, selectionArgs);
    } else {
      LOG.debug("location not found:" + target);
    }

    sqlDb.close();
  }

  /**
   * insert a new observation
   * @param observationModel
   * @param context
   * @return
   */
  public Long newObservation(ObservationModel observationModel, Context context) {
    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getWritableDatabase();
    ContentValues contentValues = observationModel.toContentValues(context);
    Long rowId = sqlDb.insert(ObservationTable.TABLE_NAME, null, contentValues);
    LOG.debug("new observation:" + rowId);
    sqlDb.close();
    return rowId;
  }

  /**
   * discover sorties
   * @param context
   * @return list of sorties w/location rows to upload
   */
  public StringList selectLocationSorties(Context context) {
    StringList results = new StringList();

    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getReadableDatabase();

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

    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getReadableDatabase();

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

    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getReadableDatabase();

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

    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getReadableDatabase();

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
    DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
    SQLiteDatabase sqlDb = dataBaseHelper.getReadableDatabase();

    for(LocationModel locationModel:locationModelList) {
      String whereClause = "_id=?";
      String[] whereArgs = new String[]{locationModel.getId().toString()};
      sqlDb.delete(LocationTable.TABLE_NAME, whereClause, whereArgs);
    }

    sqlDb.close();
  }
}
