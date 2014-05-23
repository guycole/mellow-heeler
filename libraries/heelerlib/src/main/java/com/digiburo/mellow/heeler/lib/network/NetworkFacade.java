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
   * Remote service configuration is obtained from a remote server.
   * Configuration rarely changes.  Should be read prior to uploading collected datum.
   * If the configuration version has changed, then load fresh values into user preferences.
   * @param networkListener
   * @param context
   */
  public void readRemoteConfiguration(final NetworkListener networkListener, final Context context) {
    RemoteConfigurationWriter remoteConfigurationWriter = new RemoteConfigurationWriter();
    remoteConfigurationWriter.doJsonGet(networkListener, context);
  }

  /**
   * Discover if this installation is authorized to use remote server
   * @param networkListener
   * @param context
   */
  public void testAuthorization(final NetworkListener networkListener, final Context context) {
    AuthorizationWriter authorizationWriter = new AuthorizationWriter();
    authorizationWriter.doJsonPost(networkListener, context);
  }

  /**
   * Write collected locations
   * @param sortieUuid
   * @param locationModelList
   * @param networkListener
   * @param context
   */
  public void writeLocations(final String sortieUuid, final LocationModelList locationModelList, final NetworkListener networkListener, final Context context) {
    GeoLocationWriter geoLocationWriter = new GeoLocationWriter();
    geoLocationWriter.doJsonPost(sortieUuid, locationModelList, networkListener, context);
  }

  /**
   *
   * @param sortieUuid
   * @param observationModelList
   * @param networkListener
   * @param context
   */
  public void writeObservations(final String sortieUuid, final ObservationModelList observationModelList, final NetworkListener networkListener, final Context context) {
    ObservationWriter observationWriter = new ObservationWriter();
    observationWriter.doJsonPost(sortieUuid, observationModelList, networkListener, context);
  }

  /**
   *
   * @param sortieModel
   * @param networkListener
   * @param context
   */
  public void writeSortie(final SortieModel sortieModel, final NetworkListener networkListener, final Context context) {
    SortieWriter sortieWriter = new SortieWriter();
    sortieWriter.doJsonPost(sortieModel, networkListener, context);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */