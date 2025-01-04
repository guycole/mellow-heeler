package net.braingang.heeler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Models act as containers, bridge between database and code
 * Children should implement equals()/hashCode() without comparing row ID
 * @author gsc
 */
public interface DataBaseModelIf {

  /**
   * set reasonable model defaults
   */
  void setDefault();

  /**
   * load content from model
   * @return populated values
   */
  ContentValues toContentValues();

  /**
   * convert from cursor to model
   * @param cursor points to model datum
   */
  void fromCursor(final Cursor cursor);

  /**
   * return associated table name
   * @return associated table name
   */
  String getTableName();

  /**
   * return associated table URI
   * @return associated table URI
   */
  Uri getTableUri();

  //for BaseColumns
  Long getId();
  void setId(final Long id);
}