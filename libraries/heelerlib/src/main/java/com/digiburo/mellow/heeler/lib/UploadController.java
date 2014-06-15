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

import java.util.Date;

/**
 * upload contents of current database
 * only uploads items which have not yet been transmitted
 * uploads by sortie, i.e. location, observation then sortie
 * @author gsc
 */
public class UploadController implements NetworkListener {
  public static final int MAX_ROWS = 5;
  public static final int MAX_COUNT = 12*2;

  private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

  private enum ResponseOption { WAITING, FAILURE, SUCCESS };
  private volatile ResponseOption authorizationResponseOption = ResponseOption.WAITING;
  private volatile ResponseOption configurationResponseOption = ResponseOption.WAITING;
  private ResponseOption locationResponseOption = ResponseOption.WAITING;
  private ResponseOption observationResponseOption = ResponseOption.WAITING;
  private ResponseOption sortieResponseOption = ResponseOption.WAITING;

  private enum StateOption { CONFIGURATION, AUTHORIZE, LOCATION, OBSERVATION, SORTIE, DONE };

  private Context context;
  private DataBaseFacade dataBaseFacade;
  private final NetworkFacade networkFacade = new NetworkFacade();

  private SortieModelList sortieModelList;
  private int sortieNdx;

  /**
   * ensure remote server will accept upload
   * @param authorizationResponse
   */
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    LOG.debug("fresh authorization noted");

    if (authorizationResponse == null) {
      authorizationResponseOption = ResponseOption.FAILURE;
    } else {
      if (Constant.OK.equals(authorizationResponse.getStatus())) {
        authorizationResponseOption = ResponseOption.SUCCESS;
      } else {
        authorizationResponseOption = ResponseOption.FAILURE;
      }
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

    /*
    if (Constant.OK.equals(observationResponse.getStatus())) {
      if (++sortieNdx < sortieModelList.size()) {
        dispatcher(StateOption.LOCATION);
      } else {
        LOG.info("all data uploaded");
      }
    } else {
      // observation failure
    }
    */
  }

  /**
   * refresh remote configuration
   * @param remoteConfigurationResponse
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    System.out.println("xoxoxoxoxoxoxoxxo");
    if (remoteConfigurationResponse == null) {
      configurationResponseOption = ResponseOption.FAILURE;
    } else {
      configurationResponseOption = ResponseOption.SUCCESS;
    }
  }

  /**
   * success sortie write
   * @param observationResponse
   */
  public void freshSortie(final SortieResponse sortieResponse) {
    LOG.debug("fresh sortie noted:" + sortieResponse.getStatus());

    if (Constant.OK.equals(sortieResponse.getStatus())) {
      writeObservation();
    } else {
      // observation failure
    }
  }

  /**
   * upload everything, collection must be stopped prior to invoking
   * @param context
   */
  public void uploadAll(final Context arg) {
    LOG.debug("uploadAll");

    context = arg;


  /*
    public static void startActionFoo(Context context, String param1, String param2) {

    }
  */

    /*
    dataBaseFacade = new DataBaseFacade(context);
    sortieModelList = dataBaseFacade.selectAllSorties(false, context);

    if (sortieModelList.isEmpty()) {
      LOG.debug("uploadAll empty list");
      return;
    }
    */

    /*
    if (!testConfigurationAuthorization()) {
      LOG.debug("uploadAll fail config/auth");
      return;
    }


    /*
    for (SortieModel sortieModel:sortieModelList) {
      StateOption currentOption = StateOption.CONFIGURATION;
      do {
        currentOption = dispatcher(currentOption, sortieModel);
      } while (currentOption != StateOption.DONE);
    }
   */
  }

  private boolean testConfigurationAuthorization() {
    StateOption stateOption = readConfiguration();
    LOG.info("back:" + stateOption);
    return true;
  }



  private StateOption dispatcher(StateOption currentOption, SortieModel sortieModel) {
    LOG.info("dispatch option:" + currentOption + ":" + sortieModel.getSortieUuid());

    /*
    switch(currentOption) {
      case CONFIGURATION:
        currentOption = writeConfiguration();
        break;
      case AUTHORIZE:
        currentOption = writeAuthorization();
        break;
      case LOCATION:
        writeLocation();
        break;
      case OBSERVATION:
        writeObservation();
        break;
      case SORTIE:
        writeSortie();
        break;
      case DONE:
        return currentOption;
        break;
    }
    */

    return StateOption.DONE;
  }

  private StateOption writeAuthorization() {
    /*
    networkFacade.testAuthorization(this, context);

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
      } catch(Exception exception) {
        //empty
      }
    } while ((testCount < 12) && (authorizationResponseOption == ResponseOption.WAITING));

    if (authorizationResponseOption == ResponseOption.SUCCESS) {
      LOG.info("authorization success noted, return location");
      return StateOption.DONE;
    }

    LOG.info("authorization failure noted, return done");
    return StateOption.DONE;
    */
    return StateOption.DONE;
  }

  private StateOption readConfiguration() {
    networkFacade.readRemoteConfiguration(this, context);

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
        System.out.println("sleeping:" + testCount);
      } catch(Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (configurationResponseOption == ResponseOption.WAITING));

    if (configurationResponseOption == ResponseOption.SUCCESS) {
      LOG.info("configuration success noted, return authorize");
      return StateOption.AUTHORIZE;
    }

    LOG.info("configuration failure noted, return done");
    return StateOption.DONE;
  }

  private void writeLocation() {
    /*
    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    String sortieUuid = sortieModel.getSortieUuid();

    LocationModelList locationModelList = null;
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
    */
  }

  private void writeObservation() {
    /*
    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    String sortieUuid = sortieModel.getSortieUuid();

    ObservationModelList observationModelList = null;
    ObservationModelList tempList = dataBaseFacade.selectAllObservations(false, sortieUuid, context);
    if (tempList.isEmpty()) {
      dispatcher(StateOption.SORTIE);
    } else if (tempList.size() < MAX_ROWS) {
      observationModelList = tempList;
    } else {
      for (int ii = 0; ii < MAX_ROWS; ii++) {
        observationModelList.add(tempList.get(ii));
      }
    }

    networkFacade.writeObservations(sortieUuid, observationModelList, this, context);
    */
  }

  private void writeSortie() {
    /*
    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    networkFacade.writeSortie(sortieModel, this, context);
    */
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */