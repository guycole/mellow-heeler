package com.digiburo.mellow.heeler.lib.utility;

/**
 * @author gsc
 */
public enum LegalJsonMessage {
  UNKNOWN("Unknown"),
  AUTHORIZATION("Authorization"),
  CONFIGURATION("Configuration"),
  GEOLOCATION("GeoLocation"),
  OBSERVATION("Observation"),
  SORTIE("Sortie");

  private String name;

  /**
   * ctor
   * @param arg
   */
  LegalJsonMessage(String arg) {
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
  public static LegalJsonMessage discoverMatchingEnum(String arg) {
    LegalJsonMessage result = LegalJsonMessage.UNKNOWN;

    if (arg == null) {
      return result;
    }

    for (LegalJsonMessage symbol:LegalJsonMessage.values()) {
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