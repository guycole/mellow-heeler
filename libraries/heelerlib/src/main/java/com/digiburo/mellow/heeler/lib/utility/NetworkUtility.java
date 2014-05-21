package com.digiburo.mellow.heeler.lib.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Network related helpers
 *
 * @author gsc
 */
public class NetworkUtility {

  //
  private static final Logger LOG = LoggerFactory.getLogger(NetworkUtility.class);

  /**
   * return true if network connectivity is available
   * 
   * @param context
   * @return true if network connectivity is available
   */
  public static boolean isNetworkAvailable(final Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) {
      //mobile packet data not enabled
      return(false);
    }
 
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    if (networkInfo == null) {
      //no active network
      return(false);
    }    
    
    return(networkInfo.isConnected());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
