package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class NetworkFacade {
  private static final Logger LOG = LoggerFactory.getLogger(NetworkFacade.class);

  /**
   * Remote configuration is obtained from a remote server.
   * Configuration rarely changes.  Should be read prior to uploading collected datum.
   * If the configuration version has changed, then load fresh values into user preferences.
   * @param handlerClass
   * @param context
   */
  public void readRemoteConfiguration(final Class handlerClass, final Context context) {
    RemoteConfigurationWriter remoteConfigurationWriter = new RemoteConfigurationWriter();
    remoteConfigurationWriter.doJsonGet(handlerClass, context);
  }

  /**
   * Discover if this installation is authorized to use remote server
   * @param handlerClass
   * @param context
   */
  public void testAuthorization(final Class handlerClass, final Context context) {
    AuthorizationWriter authorizationWriter = new AuthorizationWriter();
    authorizationWriter.doJsonPost(handlerClass, context);
  }

  /**
   * Write collected locations
   * @param sortieUuid
   * @param locationModelList
   * @param handlerClass
   * @param context
   */
  public void writeLocations(final String sortieUuid, final LocationModelList locationModelList, final Class handlerClass, final Context context) {
    GeoLocationWriter geoLocationWriter = new GeoLocationWriter();
    geoLocationWriter.doJsonPost(sortieUuid, locationModelList, handlerClass, context);
  }

  /**
   *
   * @param sortieUuid
   * @param observationModelList
   * @param handlerClass
   * @param context
   */
  public void writeObservations(final String sortieUuid, final ObservationModelList observationModelList, final Class handlerClass, final Context context) {
    ObservationWriter observationWriter = new ObservationWriter();
    observationWriter.doJsonPost(sortieUuid, observationModelList, handlerClass, context);
  }

  /**
   *
   * @param sortieModel
   * @param context
   */
  public void writeSortie(final SortieModel sortieModel, final Class handlerClass, final Context context) {
    SortieWriter sortieWriter = new SortieWriter();
    sortieWriter.doJsonPost(sortieModel, handlerClass, context);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */