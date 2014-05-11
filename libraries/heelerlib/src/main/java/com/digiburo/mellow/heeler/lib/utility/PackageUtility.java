package com.digiburo.mellow.heeler.lib.utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Discover version
 * @author gsc
 */
public class PackageUtility {

  /**
   * Return version code
   * @param context
   * @return version code
   */
  public static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException exception) {
      throw new RuntimeException("package name failure: " + exception);
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
