package com.digiburo.mellow.heeler.lib.utility;

import android.text.format.Time;

import com.digiburo.mellow.heeler.lib.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * time related helpers
 *
 * @author gsc
 */
public class TimeUtility {

  /**
   * Return time
   * @return return current time
   */
  public static Time timeNow() {
    Time time = new Time();
    time.setToNow();
    return(time);
  }

  /**
   *
   * @return
   */
  public static long timeMillis() {
    Time time = timeNow();
    return(time.toMillis(Constant.IGNORE_DST));
  }

  /**
   *
   * @param arg
   * @return
   */
  public static long timeMillis(Time arg) {
    return(arg.toMillis(Constant.IGNORE_DST));
  }

  /**
   *
   * @param arg
   * @return
   */
  public static Time timeMillis(long arg) {
    Time time = new Time();
    time.set(arg);
    time.normalize(Constant.IGNORE_DST);
    return(time);
  }

  /**
   * Time.equals() does not reliably work
   *
   * @param arg1
   * @param arg2
   * @return
   */
  public static boolean timeEquals(Time arg1, Time arg2) {
    if ((arg1 == null) || (arg2 == null)) {
      return(false);
    }

    if (arg1.toMillis(Constant.IGNORE_DST) == arg2.toMillis(Constant.IGNORE_DST)) {
      return(true);
    }

    return(false);
  }

  /**
   * Return formatted date string
   *
   * @param arg
   * @return
   */
  public static String dateFormat(Time arg) {
    Date date = new Date();
    date.setTime(arg.toMillis(Constant.IGNORE_DST));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return(sdf.format(date));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
