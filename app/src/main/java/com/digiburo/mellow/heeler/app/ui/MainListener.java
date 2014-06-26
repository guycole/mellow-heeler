package com.digiburo.mellow.heeler.app.ui;

import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

/**
 * @author gsc
 */
public interface MainListener {

  /**
   * add to hot list
   * @param observationModel
   */
  void addHot(ObservationModel observationModel);

  /**
   * display google map
   * @param locationModel
   */
  void displayGoogleMap(LocationModel locationModel);

  /**
   * display google map
   * @param observationModel
   */
  void displayGoogleMap(ObservationModel observationModel);

  /**
   * display google map
   * @param sortieModel
   */
  void displayGoogleMap(SortieModel sortieModel);

  /**
   * display location detail
   * @param uuid
   */
  void displayLocationDetail(String uuid);

  /**
   * display observation detail
   * @param uuid
   */
  void displayObservationDetail(String uuid);

  /**
   * display sortie detail
   * @param uuid
   */
  void displaySortieDetail(String uuid);

  /**
   * start collection
   */
  void sortieStart(final String sortieName);

  /**
   * stop collection
   */
  void sortieStop();
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */