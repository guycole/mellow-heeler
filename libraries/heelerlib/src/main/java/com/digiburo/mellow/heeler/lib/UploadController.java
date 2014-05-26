package com.digiburo.mellow.heeler.lib;

import android.content.Context;
import android.content.Intent;

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
  private final NetworkFacade networkFacade = new NetworkFacade();

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

      Intent notifier = new Intent(Constant.UPLOAD_EVENT);
      notifier.putExtra(Constant.INTENT_AUTH_FLAG, true);
      context.sendBroadcast(notifier);

      sortieUpload();
    } else {
      LOG.debug("remote authorization failure");

      Intent notifier = new Intent(Constant.UPLOAD_EVENT);
      notifier.putExtra(Constant.INTENT_AUTH_FLAG, false);
      context.sendBroadcast(notifier);
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
    } else {
      Intent notifier = new Intent(Constant.UPLOAD_EVENT);
      notifier.putExtra(Constant.INTENT_UPLOAD_FLAG, true);
      context.sendBroadcast(notifier);
    }
  }

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final SortieModelList sortieModelList, final Context context) {
    LOG.debug("uploadAll");

    this.sortieModelList = sortieModelList;
    this.context = context;

    dataBaseFacade = new DataBaseFacade(context);
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