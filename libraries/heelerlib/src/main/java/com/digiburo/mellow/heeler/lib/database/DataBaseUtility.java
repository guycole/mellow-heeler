package com.digiburo.mellow.heeler.lib.database;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

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
//      Toast.makeText(context, "db on internal fs", Toast.LENGTH_LONG).show();
      return DataBaseHelper.DATABASE_FILE_NAME;
    } else {
      // example /storage/emulated/0/Android/data/com.digiburo.mellow.heeler/files/heeler.db
      // example /storage/emulated/0/Android/data/com.digiburo.mellow.heeler/files/heeler.db
      // example             /sdcard/Android/data/com.digiburo.mellow.heeler/files/heeler.db
      //                     /sdcard/Android/data/com.digiburo.mellow.heeler/files
      //                 /mnt/sdcard/Android/data/com.digiburo.mellow.heeler/files/heeler.db

      String result = DataBaseHelper.DATABASE_FILE_NAME;
      if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//        Toast.makeText(context, "db on external fs", Toast.LENGTH_LONG).show();
        result = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + DataBaseHelper.DATABASE_FILE_NAME;
      } else {
 //       Toast.makeText(context, "db on internal fs", Toast.LENGTH_LONG).show();
      }

      return result;
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 11, 2014 by gsc
 */