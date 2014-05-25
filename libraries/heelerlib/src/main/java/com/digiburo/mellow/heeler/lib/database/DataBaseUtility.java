package com.digiburo.mellow.heeler.lib.database;

import android.content.Context;

import java.io.File;

/**
 * @author gsc
 */
public class DataBaseUtility {

  /**
   * return database filename
   * @param internalFlag true, use internal file system else external
   * @param context
   * @return file name
   */
  public static String dataBaseFileName(boolean internalFlag, final Context context) {
    if (internalFlag) {
      return DataBaseHelper.DATABASE_FILE_NAME;
    } else {
      return context.getExternalFilesDir(null).getAbsolutePath() + File.separator + DataBaseHelper.DATABASE_FILE_NAME;
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */