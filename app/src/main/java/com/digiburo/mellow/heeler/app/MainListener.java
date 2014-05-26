package com.digiburo.mellow.heeler.app;

/**
 * @author gsc
 */
public interface MainListener {

  /**
   * switch sortie list for google map
   * @param rowId sortieModel rowId
   */
  void displayGoogleMap(long rowId);

  /**
   * current sortie has reached threshold limits
   * @param name
   */
  void restartSortie(String name);
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */