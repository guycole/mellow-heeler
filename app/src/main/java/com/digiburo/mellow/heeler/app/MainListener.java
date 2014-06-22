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
   * stop collection
   */
  void sortieStop();
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */