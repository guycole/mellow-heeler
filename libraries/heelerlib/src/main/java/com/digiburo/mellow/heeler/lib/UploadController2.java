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
public class UploadController2 implements NetworkListener {
  public static final int MAX_ROWS = 50;

  private static final Logger LOG = LoggerFactory.getLogger(UploadController2.class);

  private enum StateOption { CONFIGURATION, AUTHORIZE, LOCATION };
  //private StateOption = CONFIGURATION;

  private Context context;
  private DataBaseFacade dataBaseFacade;
  private final NetworkFacade networkFacade = new NetworkFacade();

  private boolean locationFlag = false;
  private boolean observationFlag = false;

  private int sortieModelNdx = 0;
  private SortieModelList sortieModelList;

  private int locationModelNdx = 0;
  private LocationModelList locationModelList;

  private int observationModelNdx = 0;
  private ObservationModelList observationModelList;

  /**
   * NetworkListener:updated remote configuration
   * @param remoteConfigurationResponse
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    LOG.debug("remote configuration noted");
    networkFacade.testAuthorization(this, context);
  }

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final Context context) {
    LOG.debug("uploadAll");

    this.context = context;

    dataBaseFacade = new DataBaseFacade(context);

    /*
    sortieModelList = dataBaseFacade.selectAllSorties(false, context);
    for (SortieModel sortieModel:sortieModelList) {
      uploadLocation(sortieModel.getSortieUuid());
    }
    */

    networkFacade.readRemoteConfiguration(this, context);

    /*
    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    */
  }

  private void dispatcher() {

  }

  ////////////

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

    /*
    locationFlag = true;

    if (testForSortieComplete()) {
      sortieWrapUp();
    }
    */
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


  private void sortieUpload() {
    locationFlag = false;
    observationFlag = false;

    SortieModel model = sortieModelList.get(sortieModelNdx);
    uploadLocation(model.getSortieUuid());
//    uploadObservation(model.getSortieUuid());
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
//    LocationModelList tempList = dataBaseFacade.selectAllLocations(false, sortieUuid, context);
//    LOG.debug("uploadLocation:" + tempList.size() + ":" + sortieUuid);

    /*
    if (tempList.isEmpty()) {
      locationFlag = true;
    } else {
      locationModelList = new LocationModelList();
      for (int ii = 0; (ii < MAX_ROWS) || (ii < tempList.size()); ii++) {
        locationModelList.add(tempList.get(ii));
      }

      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeLocations(sortieUuid, locationModelList, this, context);
    }*/

    /*
    if (locationModelList.isEmpty()) {
      locationFlag = true;

      if (testForSortieComplete()) {
        sortieWrapUp();
      }
    } else {

    }
    */
  }

  private void uploadObservation(final String sortieUuid) {
    ObservationModelList observationModelList = dataBaseFacade.selectAllObservations(false, sortieUuid, context);
    LOG.debug("uploadObservation:" + observationModelList.size() + ":" + sortieUuid);
    if (observationModelList.isEmpty()) {
      observationFlag = true;

      if (testForSortieComplete()) {
        sortieWrapUp();
      }
    } else {
      NetworkFacade networkFacade = new NetworkFacade();
      networkFacade.writeObservations(sortieUuid, observationModelList, this, context);
    }
  }

  /**
   *
   * @param sortieModel
   */
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