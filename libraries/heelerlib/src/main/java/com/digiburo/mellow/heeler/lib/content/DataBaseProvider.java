package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.digiburo.mellow.heeler.lib.Constant;

/**
 * @author gsc
 */
public class DataBaseProvider extends ContentProvider {

  //
  public final String LOG_TAG = getClass().getName();

  //
  private static final UriMatcher uriMatcher;

  //URI Matcher Targets
  private static final int URI_MATCH_OBSERVATIONS = 10;
  private static final int URI_MATCH_OBSERVATION_ID = 11;

  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    uriMatcher.addURI(Constant.AUTHORITY, ObservationTable.TABLE_NAME, URI_MATCH_OBSERVATIONS);
    uriMatcher.addURI(Constant.AUTHORITY, ObservationTable.TABLE_NAME + "/#", URI_MATCH_OBSERVATION_ID);
  }

  //
  private DataBaseHelper dbHelper;

  @Override
  public boolean onCreate() {
    dbHelper = new DataBaseHelper(getContext());
    return(true);
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
      case URI_MATCH_OBSERVATIONS:
        return(ObservationTable.CONTENT_TYPE);
      case URI_MATCH_OBSERVATION_ID:
        return(ObservationTable.CONTENT_ITEM_TYPE);
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count = 0;
    String id = "";

    switch (uriMatcher.match(uri)) {
      case URI_MATCH_OBSERVATIONS:
        count = db.delete(ObservationTable.TABLE_NAME, selection, selectionArgs);
        break;
      case URI_MATCH_OBSERVATION_ID:
        id = uri.getPathSegments().get(1);
        count = db.delete(ObservationTable.TABLE_NAME, ObservationTable.Columns._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return(count);
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long rowId = 0;

    switch (uriMatcher.match(uri)) {
      case URI_MATCH_OBSERVATIONS:
        rowId = db.insert(ObservationTable.TABLE_NAME, null, values);
        if (rowId > 0) {
          Uri result = ContentUris.withAppendedId(ObservationTable.CONTENT_URI, rowId);
          getContext().getContentResolver().notifyChange(ObservationTable.CONTENT_URI, null);
          return(result);
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    throw new SQLException("insert failure:" + uri);
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    String orderBy = sortOrder;

    switch (uriMatcher.match(uri)) {
      case URI_MATCH_OBSERVATIONS:
        qb.setTables(ObservationTable.TABLE_NAME);
        qb.setProjectionMap(ObservationTable.PROJECTION_MAP);
        if (sortOrder == null) {
          orderBy = ObservationTable.DEFAULT_SORT_ORDER;
        }
        break;
      case URI_MATCH_OBSERVATION_ID:
        qb.setTables(ObservationTable.TABLE_NAME);
        qb.setProjectionMap(ObservationTable.PROJECTION_MAP);
        qb.appendWhere(ObservationTable.Columns._ID + "=" + uri.getPathSegments().get(1));
        if (sortOrder == null) {
          orderBy = ObservationTable.DEFAULT_SORT_ORDER;
        }
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    // Get the database and run the query
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cc = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

    // Tell the cursor what uri to watch, so it knows when its source data changes
    cc.setNotificationUri(getContext().getContentResolver(), uri);
    return(cc);
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count = 0;
    String id = "";

    switch (uriMatcher.match(uri)) {
      case URI_MATCH_OBSERVATIONS:
        count = db.update(ObservationTable.TABLE_NAME, values, selection, selectionArgs);
        break;
      case URI_MATCH_OBSERVATION_ID:
        id = uri.getPathSegments().get(1);
        count = db.update(ObservationTable.TABLE_NAME, values, ObservationTable.Columns._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        break;
      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return(count);
  }
}
