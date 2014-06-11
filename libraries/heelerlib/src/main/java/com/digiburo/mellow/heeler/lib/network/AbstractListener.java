package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public abstract class AbstractListener implements NetworkListener {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractListener.class);

  private final Context context;
  private final NetworkFacade networkFacade = new NetworkFacade();

  AuthorizationResponse authorizationResponse = null;
  GeoLocationResponse geoLocationResponse = null;
  ObservationResponse observationResponse = null;
  RemoteConfigurationResponse remoteConfigurationResponse = null;
  SortieResponse sortieResponse = null;

  /**
   *
   * @param arg
   */
  public AbstractListener(Context arg) {
    context = arg;
  }

  /**
   * ensure remote server will accept upload
   * @param authorizationResponse
   */
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    LOG.debug("remote authorization noted");
    this.authorizationResponse = authorizationResponse;
  }

  /**
   *
   * @return
   */
  public AuthorizationResponse getAuthorizationResponse() {
    return authorizationResponse;
  }

  /**
   * success location write
   * @param geoLocationResponse
   */
  public void freshGeoLocation(final GeoLocationResponse geoLocationResponse) {
    LOG.debug("remote geoloc noted");
    this.geoLocationResponse = geoLocationResponse;
  }

  /**
   *
   * @return
   */
  public GeoLocationResponse getGeoLocationResponse() {
    return geoLocationResponse;
  }

  /**
   * success observation write
   * @param observationResponse
   */
  public void freshObservation(final ObservationResponse observationResponse) {
    LOG.debug("remote observation noted");
    this.observationResponse = observationResponse;
  }

  /**
   *
   * @return
   */
  public ObservationResponse getObservationResponse() {
    return observationResponse;
  }

  /**
   * refresh remote configuration
   * @param remoteConfigurationResponse
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    LOG.debug("remote configuration noted");
    this.remoteConfigurationResponse = remoteConfigurationResponse;
  }

  /**
   *
   * @return
   */
  public RemoteConfigurationResponse getRemoteConfigurationResponse() {
    return remoteConfigurationResponse;
  }

  /**
   * success sortie write
   * @param observationResponse
   */
  public void freshSortie(final SortieResponse sortieResponse) {
    LOG.debug("remote sortie noted");
    this.sortieResponse = sortieResponse;
  }

  /**
   *
   * @return
   */
  public SortieResponse getSortieResponse() {
    return sortieResponse;
  }
}