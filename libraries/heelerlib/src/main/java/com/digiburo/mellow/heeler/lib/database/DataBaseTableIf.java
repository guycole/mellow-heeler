package com.digiburo.mellow.heeler.lib.database;

/**
 * common parent for tables
 * @author gsc
 */
public interface DataBaseTableIf {

  /**
   * return associated table name
   * @return associated table name
   */
  String getTableName();

  /**
   * return default sort order
   * @return default sort order
   */
  String getDefaultSortOrder();

  /**
   * return default projection (column names)
   * @return return default projection (column names)
   */
  String[] getDefaultProjection();
}

/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */

