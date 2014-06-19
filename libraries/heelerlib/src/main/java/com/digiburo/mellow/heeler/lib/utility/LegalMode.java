package com.digiburo.mellow.heeler.lib.utility;

/**
 * @author gsc
 */
public enum LegalMode {
  UNKNOWN("Unknown"),
  COLLECTION("Collection"),
  IDLE("Idle"),
  UPLOAD("Upload");

  private String name;

  /**
   * ctor
   * @param arg
   */
  LegalMode(String arg) {
    name = arg;
  }

  /**
   * return string of enumerated value
   * @return
   */
  @Override
  public String toString() {
    return name;
  }

  /**
   * map string to enum
   * @param arg
   * @return
   */
  public static LegalMode discoverMatchingEnum(String arg) {
    LegalMode result = LegalMode.UNKNOWN;

    if (arg == null) {
      return(result);
    }

    for (LegalMode symbol: LegalMode.values()) {
      if (symbol.name.equals(arg)) {
        result = symbol;
      }
    }

    return result;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 17, 2014 by gsc
 */