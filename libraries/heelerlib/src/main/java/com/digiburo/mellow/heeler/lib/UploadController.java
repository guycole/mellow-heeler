package com.digiburo.mellow.heeler.lib;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.digiburo.mellow.heeler.lib.network.AuthorizationResponse;
import com.digiburo.mellow.heeler.lib.network.GeoLocationResponse;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.NetworkListener;
import com.digiburo.mellow.heeler.lib.network.ObservationResponse;
import com.digiburo.mellow.heeler.lib.network.RemoteConfigurationResponse;
import com.digiburo.mellow.heeler.lib.network.SortieResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * upload contents of current database
 * only uploads items which have not yet been transmitted
 * uploads by sortie, i.e. location, observation then sortie
 * @author gsc
 */
public class UploadController implements NetworkListener {
  private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

  private Context context;
  private DataBaseFacade dataBaseFacade;
  private NetworkFacade networkFacade;

  private boolean locationFlag = false;
  private boolean observationFlag = false;

  private int sortieModelNdx = 0;
  private SortieModelList sortieModelList;

  /**
   * NetworkListener:remote authorization test
   * @param authorizationResponse
   */
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    LOG.debug("remote authorization noted");
    if (authorizationResponse.getStatus().equals("OK")) {
      LOG.debug("remote authorization approved");
      sortieUpload();
    } else {
      LOG.debug("remote authorization failure");
    }
  }

  /**
   * NetworkListener:success location write
   * @param geoLocationResponse
   */
  public void freshGeoLocation(final GeoLocationResponse geoLocationResponse) {
    LOG.debug("remote geoloc noted");
    locationFlag = true;

    if (testForSortieComplete()) {
      sortieWrapUp();
    }
  }

  /**
   * NetworkListener:success observation write
   * @param observationResponse
   */
  public void freshObservation(final ObservationResponse observationResponse) {
    LOG.debug("remote observation noted");
    observationFlag = true;

    if (testForSortieComplete()) {
      sortieWrapUp();
    }
  }

  /**
   * NetworkListener:updated remote configuration
   * @param remoteConfigurationResponse
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    LOG.debug("remote configuration noted");
    networkFacade.testAuthorization(this, context);
  }

  /**
   * NetworkListener:success sortie write
   * @param sortieResponse
   */
  public void freshSortie(final SortieResponse sortieResponse) {
    LOG.debug("remote sortie noted");

    if (++sortieModelNdx < sortieModelList.size()) {
      sortieUpload();
    }
  }

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final Context context) {
    LOG.debug("uploadAll");

    dataBaseFacade = new DataBaseFacade(context);
    sortieModelList = dataBaseFacade.selectAllSorties(false, context);
    if (sortieModelList.isEmpty()) {
      LOG.debug("empty sortie list");
      return;
    }

    this.context = context;

    networkFacade = new NetworkFacade();
    networkFacade.readRemoteConfiguration(this, context);
  }

  private void sortieUpload() {
    locationFlag = false;
    observationFlag = false;

    SortieModel model = sortieModelList.get(sortieModelNdx);
    uploadLocation(model.getSortieUuid());
    uploadObservation(model.getSortieUuid());
  }

  private boolean testForSortieComplete() {
    if (locationFlag && observationFlag) {
      return true;
    }

    return false;
  }

  private void sortieWrapUp() {
    SortieModel model = sortieModelList.get(sortieModelNdx);
    uploadSortie(model);
  }

  private void uploadLocation(final String sortieUuid) {
    LOG.debug("uploadLocation:" + sortieUuid);

    LocationModelList locationModelList = dataBaseFacade.selectAllLocations(false, sortieUuid, context);
    if (!locationModelList.isEmpty()) {
      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeLocations(sortieUuid, locationModelList, this, context);
    }
  }

  private void uploadObservation(final String sortieUuid) {
    LOG.debug("uploadObservation:" + sortieUuid);

    ObservationModelList observationModelList = dataBaseFacade.selectAllObservations(false, sortieUuid, context);
    if (!observationModelList.isEmpty()) {
      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeObservations(sortieUuid, observationModelList, this, context);
    }
  }

  private void uploadSortie(final SortieModel sortieModel) {
    LOG.debug("uploadSortie:" + sortieModel.getSortieUuid());
    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.writeSortie(sortieModel, this, context);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */