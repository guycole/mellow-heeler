package net.braingang.heeler;

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