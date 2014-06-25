package com.digiburo.mellow.heeler.lib.utility;

/**
 * @author gsc
 */
public class UuidHelper {

  /**
   * Break a UUID up nicely near hyphens 70b9eb3f-2f11-4592-beba-e50e0629079b
   * @param arg
   * @return
   */
  public static String formatUuidString(final String arg) {
    int ndx1 = arg.indexOf("-");
    int ndx2 = arg.lastIndexOf("-");

    String temp1 = arg.substring(0, ndx1+1);
    String temp2 = arg.substring(ndx1+1, ndx2+1);
    String temp3 = arg.substring(ndx2+1);

    return temp1 + "\n" + temp2 + "\n" + temp3;
  }
}
