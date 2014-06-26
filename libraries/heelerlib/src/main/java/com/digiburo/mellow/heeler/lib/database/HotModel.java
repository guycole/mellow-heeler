package com.digiburo.mellow.heeler.lib.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * important stations
 * @author gsc
 */
public class HotModel implements DataBaseModelIf {
  private Long id;
  private String ssid;
  private String bssid;

  @Override
  public void setDefault() {
    id = 0L;
    ssid = Constant.UNKNOWN;
    bssid = Constant.UNKNOWN;
  }

  @Override
  public void fromCursor(final Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex(ObservationTable.Columns._ID));
    bssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.BSSID));
    ssid = cursor.getString(cursor.getColumnIndex(ObservationTable.Columns.SSID));
  }

  @Override
  public ContentValues toContentValues() {
    ContentValues cv = new ContentValues();
    cv.put(ObservationTable.Columns.BSSID, bssid);
    cv.put(ObservationTable.Columns.SSID, ssid);
    return cv;
  }

  @Override
  public String getTableName() {
    return HotTable.TABLE_NAME;
  }

  @Override
  public Uri getTableUri() {
    return HotTable.CONTENT_URI;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  public String getSsid() {
    return ssid;
  }

  public void setSsid(String ssid) {
    this.ssid = ssid;
  }

  public String getBssid() {
    return bssid;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HotModel hotModel = (HotModel) o;

    if (!bssid.equals(hotModel.bssid)) return false;
    if (!ssid.equals(hotModel.ssid)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = ssid.hashCode();
    result = 31 * result + bssid.hashCode();
    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 22, 2014 by gsc
 */