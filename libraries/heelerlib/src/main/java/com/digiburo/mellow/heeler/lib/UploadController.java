package com.digiburo.mellow.heeler.lib;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.digiburo.mellow.heeler.lib.network.GeoLocationWriter;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.ObservationWriter;
import com.digiburo.mellow.heeler.lib.utility.StringList;
import com.digiburo.mellow.heeler.lib.utility.NetworkUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class UploadController {
  private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

  private DataBaseFacade dataBaseFacade;
  private NetworkFacade networkFacade;

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final Context context) {
    LOG.debug("uploadAll");

    networkFacade = new NetworkFacade();
    networkFacade.readRemoteConfiguration(context);
    networkFacade.testAuthorization(context);

    dataBaseFacade = new DataBaseFacade(context);
    SortieModelList sortieModelList = dataBaseFacade.selectAllSorties(false, context);
    for (SortieModel sortieModel:sortieModelList) {
      LOG.debug("current sortie:" + sortieModel.getSortieName() + ":" + sortieModel.getSortieUuid());
      uploadLocation(sortieModel.getSortieUuid(), context);
      uploadObservation(sortieModel.getSortieUuid(), context);
      uploadSortie(sortieModel, context);
    }
  }

  private void uploadLocation(final String sortieUuid, final Context context) {
    LOG.debug("uploadLocation:" + sortieUuid);

    LocationModelList locationModelList = dataBaseFacade.selectAllLocations(false, sortieUuid, context);
    if (!locationModelList.isEmpty()) {
      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeLocations(sortieUuid, locationModelList, context);
    }
  }

  private void uploadObservation(final String sortieUuid, final Context context) {
    LOG.debug("uploadObservation:" + sortieUuid);

    ObservationModelList observationModelList = dataBaseFacade.selectAllObservations(false, sortieUuid, context);
    if (!observationModelList.isEmpty()) {
      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeObservations(sortieUuid, observationModelList, context);
    }
  }

  private void uploadSortie(final SortieModel sortieModel, final Context context) {
    LOG.debug("uploadSortie:" + sortieModel.getSortieUuid());
    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.writeSortie(sortieModel, context);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */