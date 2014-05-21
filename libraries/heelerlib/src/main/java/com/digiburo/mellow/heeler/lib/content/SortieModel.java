package com.digiburo.mellow.heeler.lib.content;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * sortie detail
 * @author gsc
 */
public class SortieModel implements DataBaseModelIf {
  private Long id;
  private boolean uploadFlag;

  private String sortieName;
  private String sortieUuid;

  private String timeStamp;
  private long timeStampMs;

  @Override
  public void setDefault() {
    id = 0L;
    uploadFlag = false;

    sortieName = Constant.DEFAULT_SORTIE_NAME;
    sortieUuid = UUID.randomUUID().toString();

    Time timeNow = TimeUtility.timeNow();
    timeStamp = timeNow.format3339(false);
    timeStampMs = timeNow.toMillis(Constant.IGNORE_DST);
  }

  @Override
  public ContentValues toContentValues() {
    ContentValues cv = new ContentValues();

    cv.put(SortieTable.Columns.TIME_STAMP, timeStamp);
    cv.put(SortieTable.Columns.TIME_STAMP_MS, timeStampMs);
    cv.put(SortieTable.Columns.SORTIE_NAME, sortieName);
    cv.put(SortieTable.Columns.SORTIE_ID, sortieUuid);

    if (uploadFlag) {
      cv.put(SortieTable.Columns.UPLOAD_FLAG, Constant.SQL_TRUE);
    } else {
      cv.put(SortieTable.Columns.UPLOAD_FLAG, Constant.SQL_FALSE);
    }

    return(cv);
  }

  @Override
  public void fromCursor(Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex(SortieTable.Columns._ID));
    sortieName = cursor.getString(cursor.getColumnIndex(SortieTable.Columns.SORTIE_NAME));
    sortieUuid = cursor.getString(cursor.getColumnIndex(SortieTable.Columns.SORTIE_ID));
    timeStamp = cursor.getString(cursor.getColumnIndex(SortieTable.Columns.TIME_STAMP));
    timeStampMs = cursor.getLong(cursor.getColumnIndex(SortieTable.Columns.TIME_STAMP_MS));

    int temp = cursor.getInt(cursor.getColumnIndex(SortieTable.Columns.UPLOAD_FLAG));
    if (temp == Constant.SQL_TRUE) {
      uploadFlag = true;
    } else {
      uploadFlag = false;
    }
  }

  @Override
  public String getTableName() {
    return(SortieTable.TABLE_NAME);
  }

  @Override
  public Uri getTableUri() {
    return(SortieTable.CONTENT_URI);
  }

  @Override
  public String toString() {
    return(id + ":" + sortieName + ":" + sortieUuid + ":" + timeStamp);
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getSortieName() {
    return sortieName;
  }

  public void setSortieName(String sortieName) {
    this.sortieName = sortieName;
  }

  public String getSortieUuid() {
    return sortieUuid;
  }

  public void setSortieUuid(String sortieUuid) {
    this.sortieUuid = sortieUuid;
  }

  public boolean isUploadFlag() {
    return uploadFlag;
  }

  public void setUploadFlag() {
    uploadFlag = true;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  public long getTimeStampMs() {
    return timeStampMs;
  }

  public void setTimeStampMs(long timeStampMs) {
    this.timeStampMs = timeStampMs;

    Time time = new Time("UTC");
    time.set(timeStampMs);
    timeStamp = time.format3339(false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SortieModel that = (SortieModel) o;

    if (timeStampMs != that.timeStampMs) return false;
    if (uploadFlag != that.uploadFlag) return false;
    if (!sortieName.equals(that.sortieName)) return false;
    if (!sortieUuid.equals(that.sortieUuid)) return false;
    if (!timeStamp.equals(that.timeStamp)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (uploadFlag ? 1 : 0);
    result = 31 * result + sortieName.hashCode();
    result = 31 * result + sortieUuid.hashCode();
    result = 31 * result + timeStamp.hashCode();
    result = 31 * result + (int) (timeStampMs ^ (timeStampMs >>> 32));
    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */