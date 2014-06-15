package com.digiburo.mellow.heeler.lib.network;

/**
 * report completion of network transfers
 * @author gsc
 */
public interface NetworkListener {

  /**
   * ensure remote server will accept upload
   * @param authorizationResponse (or null if error)
   */
  void freshAuthorization(final AuthorizationResponse authorizationResponse);

  /**
   * success location write
   * @param geoLocationResponse (or null if error)
   */
  void freshGeoLocation(final GeoLocationResponse geoLocationResponse);

  /**
   * success observation write
   * @param observationResponse (or null if error)
   */
  void freshObservation(final ObservationResponse observationResponse);

  /**
   * refresh remote configuration
   * @param remoteConfigurationResponse (or null if error)
   */
  void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse);

  /**
   * success sortie write
   * @param sortieResponse (or null if error)
   */
  void freshSortie(final SortieResponse sortieResponse);
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */