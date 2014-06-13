package com.digiburo.mellow.heeler.lib;

import android.content.Context;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
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
  public static final int MAX_ROWS = 5;

  private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

  private enum StateOption { CONFIGURATION, AUTHORIZE, LOCATION, OBSERVATION, SORTIE };

  private Context context;
  private DataBaseFacade dataBaseFacade;
  private final NetworkFacade networkFacade = new NetworkFacade();

  private LocationModelList locationModelList;
  private ObservationModelList observationModelList;
  private SortieModelList sortieModelList;
  private int sortieNdx;

  /**
   * ensure remote server will accept upload
   * @param authorizationResponse
   */
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    LOG.debug("fresh authorization noted:" + authorizationResponse.getStatus());

    if (Constant.OK.equals(authorizationResponse.getStatus())) {
      dispatcher(StateOption.LOCATION);
    } else {
      // authorization failure
    }
  }

  /**
   * success location write
   * @param geoLocationResponse
   */
  public void freshGeoLocation(final GeoLocationResponse geoLocationResponse) {
    LOG.debug("fresh geolocation noted:" + geoLocationResponse.getStatus());

    if (Constant.OK.equals(geoLocationResponse.getStatus())) {
      writeLocation();
    } else {
      // location failure
    }
  }

  /**
   * success observation write
   * @param observationResponse
   */
  public void freshObservation(final ObservationResponse observationResponse) {
    LOG.debug("fresh observation noted:" + observationResponse.getStatus());

    if (Constant.OK.equals(observationResponse.getStatus())) {
      writeObservation();
    } else {
      // observation failure
    }
  }

  /**
   * refresh remote configuration
   * @param remoteConfigurationResponse
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    LOG.debug("remote configuration noted");
    dispatcher(StateOption.AUTHORIZE);
  }

  /**
   * success sortie write
   * @param observationResponse
   */
  public void freshSortie(final SortieResponse sortieResponse) {

  }

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final Context arg) {
    LOG.debug("uploadAll");

    context = arg;

    dataBaseFacade = new DataBaseFacade(context);
    sortieModelList = dataBaseFacade.selectAllSorties(false, context);

    if (!sortieModelList.isEmpty()) {
      dispatcher(StateOption.CONFIGURATION);
    }
  }

  private void dispatcher(StateOption currentOption) {
    switch(currentOption) {
      case CONFIGURATION:
        networkFacade.readRemoteConfiguration(this, context);
        break;
      case AUTHORIZE:
        networkFacade.testAuthorization(this, context);
        break;
      case LOCATION:
        writeLocation();
        break;
      case OBSERVATION:
        break;
      case SORTIE:
        break;
    }
  }

  private void writeLocation() {
    if (locationModelList != null) {
      //mark successful upload of previous candidates
      for (LocationModel current:locationModelList) {
        current.setUploadFlag();
        dataBaseFacade.updateLocation(current, context);
      }
    }

    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    String sortieUuid = sortieModel.getSortieUuid();

    LocationModelList tempList = dataBaseFacade.selectAllLocations(false, sortieUuid, context);
    if (tempList.isEmpty()) {
      dispatcher(StateOption.OBSERVATION);
    } else if (tempList.size() < MAX_ROWS) {
      locationModelList = tempList;
    } else {
      for (int ii = 0; ii < MAX_ROWS; ii++) {
        locationModelList.add(tempList.get(ii));
      }
    }

    networkFacade.writeLocations(sortieUuid, locationModelList, this, context);
  }

  private void writeObservation() {
    //empty
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */