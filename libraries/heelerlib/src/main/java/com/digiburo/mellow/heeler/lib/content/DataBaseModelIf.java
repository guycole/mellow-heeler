package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Models act as containers, bridge between database and code
 * Children should implement equals()/hashCode() without comparing row ID
 * @author gsc
 */
public interface DataBaseModelIf {

  /**
   * set reasonable model defaults
   *
   * @param context needed for I18N/L10N string conversion
   */
  public void setDefault(Context context);

  /**
   * load content from model
   *
   * @param context needed for I18N/L10N string conversion
   * @return populated values
   */
  public ContentValues toContentValues(Context context);

  /**
   * convert from cursor to model
   * @param cursor points to model datum
   */
  public void fromCursor(Cursor cursor);

  /**
   * return associated table name
   * @return associated table name
   */
  public String getTableName();

  //for BaseColumns
  public Long getId();
  public void setId(Long id);
}

/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */