package com.digiburo.mellow.heeler.lib.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.Time;
import android.util.TimeFormatException;

import com.digiburo.mellow.heeler.lib.Constant;

/**
 * Time class wrapper
 *
 * @author gsc
 */
public final class TimeWrapper {
  
  public TimeWrapper() {
    time = new Time();
    time.setToNow();
    initialize();
  }
  
  public TimeWrapper(long millis) {
    time = new Time();
    time.set(millis);
    initialize();
  }
  
  public TimeWrapper(Time arg) {
    time = new Time(arg);
    initialize();
  }
  
  public TimeWrapper(String dateArg, String timeArg) throws ParseException {
    //date format 2013-04-17
    //time format 21:23:29Z

    String candidate = dateArg + " " + timeArg;
 
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    Date date = sdf.parse(candidate);
    
    time = new Time();
    time.setToNow();
    time.set(date.getTime());
    
    initialize();
  }
  
  /**
   * 
   * @param arg XML formatted time stamp 2013-02-21T21:59:31.000Z
   * @throws ParseException
   */
  //public TimeWrapper(String arg) throws TimeFormatException {
  public TimeWrapper(String arg) throws ParseException {
    // two supported formats
    // 2013-02-26T18:54:40Z
    // 2013-02-26T18:54:40.123Z

    Date date = null;
    int ndx = arg.indexOf('.');
    if (ndx < 0) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      date = sdf.parse(arg);      
    } else {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      date = sdf.parse(arg);      
    }

    time = new Time();
    time.setToNow();
    time.set(date.getTime());
    /*
    System.out.println(arg);
    time = new Time();
    time.parse3339(arg);
    System.out.println(time);
*/

    initialize();
  }

  private void initialize() {
    time.normalize(Constant.IGNORE_DST);

    hour = time.hour;
    minute = time.minute;
    second = time.second;
    
    month = time.month;
    monthDay = time.monthDay;
    year = time.year;

    System.err.println("hour:" + time.hour);
    System.err.println("weekDay:" + time.weekDay);
    System.err.println("yearDay:" + time.yearDay);
    
    weekDay = time.weekDay;
    if (weekDay == 0) {
//      throw new IllegalArgumentException("tire ripper");
    }
    yearDay = time.yearDay;
  }
  
  public int getHour() {
    return hour;
  }
  public void setHour(int hour) {
    this.hour = hour;
    time.set(time.second, time.minute, hour, time.monthDay, time.month, time.year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getMinute() {
    return minute;
  }
  public void setMinute(int minute) {
    this.minute = minute;
    time.set(time.second, minute, time.hour, time.monthDay, time.month, time.year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getSecond() {
    return second;
  }
  public void setSecond(int second) {
    this.second = second;
    time.set(second, time.minute, time.hour, time.monthDay, time.month, time.year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getMonth() {
    return month;
  }
  public void setMonth(int month) {
    this.month = month;
    time.set(time.second, time.minute, time.hour, time.monthDay, month, time.year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getMonthDay() {
    return monthDay;
  }
  public void setMonthDay(int monthDay) {
    this.monthDay = monthDay;
    time.set(time.second, time.minute, time.hour, monthDay, time.month, time.year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getYear() {
    return year;
  }
  public void setYear(int year) {
    this.year = year;
    time.set(time.second, time.minute, time.hour, time.monthDay, time.month, year);
    time.normalize(Constant.IGNORE_DST);
  }
  
  public int getWeekDay() {
    return weekDay;
  }
 
  public int getYearDay() {
    return yearDay;
  }
  
  public long toMillis() {
    return time.toMillis(Constant.IGNORE_DST);
  }
  
  public String dateTimeFormat() {
    Date date = new Date();
    date.setTime(time.toMillis(Constant.IGNORE_DST));
 
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    return(df.format(date));
  }
  
  public String dateNoYearFormat() {
    Date date = new Date();
    date.setTime(time.toMillis(Constant.IGNORE_DST));

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
    return(sdf.format(date));
  }
  
  public String timeNoSecondsFormat() {
    Date date = new Date();
    date.setTime(time.toMillis(Constant.IGNORE_DST));

    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    return(sdf.format(date));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + hour;
    result = prime * result + minute;
    result = prime * result + month;
    result = prime * result + monthDay;
    result = prime * result + second;
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    
    if (obj == null)
      return false;
    
    if (getClass() != obj.getClass())
      return false;
    
    TimeWrapper other = (TimeWrapper) obj;
    
    if (hour != other.hour)
      return false;
    
    if (minute != other.minute)
      return false;
    
    if (month != other.month)
      return false;
    
    if (monthDay != other.monthDay)
      return false;
    
    if (second != other.second)
      return false;
    
    if (year != other.year)
      return false;
  
    return true;
  }
  
  public String toString() {
    return("timeWrapper:" + hour + ":" + minute + ":" + second + "//" + month + "/" + monthDay + "/" + year);
  }

  private int hour, minute, second;
  private int month, monthDay, year;
  private int weekDay, yearDay;
  
  private Time time;
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
