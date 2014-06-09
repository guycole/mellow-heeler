package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import java.util.Date;

/**
 * exercise remote configuration
 * @author gsc
 */
public class NetworkFacadeTest extends ApplicationTestCase<HeelerApplication> implements NetworkListener {
  private TestHelper testHelper = new TestHelper();

  private boolean authorizationFlag = false;
  private boolean configurationFlag = false;
  private boolean locationFlag = false;
  private boolean observationFlag = false;
  private boolean sortieFlag = false;

  public NetworkFacadeTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createApplication();
    Personality.setInternalDataBaseFileSystem(true);
    Personality.setRemoteConfigurationUrl(Constant.TEST_CONFIGURATION_URL);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    assertNotNull(authorizationResponse.getReceipt());
    assertNotNull(authorizationResponse.getTimeStamp());
    assertNotNull(authorizationResponse.getRemoteIpAddress());
    assertTrue("OK".equals(authorizationResponse.getStatus()));

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    String self = userPreferenceHelper.getAuthorizeUrl(getContext());
    assertTrue(self.equals(authorizationResponse.getLinks().getSelf().getHref()));

    authorizationFlag = true;

    startLocation();
  }

  @Override
  public void freshGeoLocation(final GeoLocationResponse geoLocationResponse) {
    assertNotNull(geoLocationResponse.getReceipt());
    assertNotNull(geoLocationResponse.getTimeStamp());
    assertNotNull(geoLocationResponse.getRemoteIpAddress());
    assertTrue("OK".equals(geoLocationResponse.getStatus()));
    assertTrue(Constant.TEST_SORTIE_ID.equals(geoLocationResponse.getSortieId()));
    assertEquals(2, geoLocationResponse.getRowCount().intValue());

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    String self = userPreferenceHelper.getLocationUrl(getContext());
    assertTrue(self.equals(geoLocationResponse.getLinks().getSelf().getHref()));

    locationFlag = true;

    startObservation();
  }

  @Override
  public void freshObservation(final ObservationResponse observationResponse) {
    assertNotNull(observationResponse.getReceipt());
    assertNotNull(observationResponse.getTimeStamp());
    assertNotNull(observationResponse.getRemoteIpAddress());
    assertTrue("OK".equals(observationResponse.getStatus()));
    assertTrue(Constant.TEST_SORTIE_ID.equals(observationResponse.getSortieId()));
    assertEquals(2, observationResponse.getRowCount().intValue());

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    String self = userPreferenceHelper.getObservationUrl(getContext());
    assertTrue(self.equals(observationResponse.getLinks().getSelf().getHref()));

    observationFlag = true;

    startSortie();
  }

  @Override
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    assertEquals(1, remoteConfigurationResponse.getMessageVersion().intValue());
    assertTrue("2014-05-19T02:18:51Z".equals(remoteConfigurationResponse.getRevisionDate()));
    assertTrue("http://digiburo.com/hal/mellow-heeler-test1.json".equals(remoteConfigurationResponse.getLinks().getSelf().getHref()));
    assertTrue("https://mellow-heeler-test.appspot.com/ws/v1/authorize".equals(remoteConfigurationResponse.getLinks().getAuthorize().getHref()));
    assertTrue("https://mellow-heeler-test.appspot.com/ws/v1/location".equals(remoteConfigurationResponse.getLinks().getLocation().getHref()));
    assertTrue("https://mellow-heeler-test.appspot.com/ws/v1/observation".equals(remoteConfigurationResponse.getLinks().getObservation().getHref()));
    assertTrue("https://mellow-heeler-test.appspot.com/ws/v1/sortie".equals(remoteConfigurationResponse.getLinks().getSortie().getHref()));

    configurationFlag = true;

    startAuthorization();
  }

  @Override
  public void freshSortie(final SortieResponse sortieResponse) {
    assertNotNull(sortieResponse.getReceipt());
    assertNotNull(sortieResponse.getTimeStamp());
    assertNotNull(sortieResponse.getRemoteIpAddress());
    assertTrue("OK".equals(sortieResponse.getStatus()));
//    assertTrue(Constant.TEST_SORTIE_ID.equals(sortieResponse.getSortieId()));
    assertNotNull(sortieResponse.getSortieId());
    assertEquals(1, sortieResponse.getRowCount().intValue());

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    String self = userPreferenceHelper.getSortieUrl(getContext());
    assertTrue(self.equals(sortieResponse.getLinks().getSelf().getHref()));

    sortieFlag = true;
  }

  private void startAuthorization() {
    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.testAuthorization(this, getContext());
  }

  private void startLocation() {
    Date date1 = new Date();
    Location location1 = new Location(LocationManager.PASSIVE_PROVIDER);
    location1.setAccuracy(1.23f);
    location1.setAltitude(2.34f);
    location1.setLatitude(3.45f);
    location1.setLongitude(4.56f);
    location1.setTime(date1.getTime());

    LocationModel locationModel1 = new LocationModel();
    locationModel1.setDefault();
    locationModel1.setLocation(location1, Constant.TEST_SORTIE_ID);

    Date date2 = new Date();
    Location location2 = new Location(LocationManager.PASSIVE_PROVIDER);
    location2.setAccuracy(5.67f);
    location2.setAltitude(6.78f);
    location2.setLatitude(7.89f);
    location2.setLongitude(8.90f);
    location2.setTime(date2.getTime());

    LocationModel locationModel2 = new LocationModel();
    locationModel2.setDefault();
    locationModel2.setLocation(location1, Constant.TEST_SORTIE_ID);

    LocationModelList locationModelList = new LocationModelList();
    locationModelList.add(locationModel1);
    locationModelList.add(locationModel2);

    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.writeLocations(Constant.TEST_SORTIE_ID, locationModelList, this, getContext());
  }

  private void startObservation() {
    ObservationModel observation1 = new ObservationModel();
    observation1.setDefault();
    observation1.setScanResult("ssid1", "bssid1", "capability1", 1111, 2222, "locationId1", Constant.TEST_SORTIE_ID);

    ObservationModel observation2 = new ObservationModel();
    observation2.setDefault();
    observation2.setScanResult("ssid2", "bssid2", "capability2", 3333, 4444, "locationId2", Constant.TEST_SORTIE_ID);

    ObservationModelList observationModelList = new ObservationModelList();
    observationModelList.add(observation1);
    observationModelList.add(observation2);

    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.writeObservations(Constant.TEST_SORTIE_ID, observationModelList, this, getContext());
  }

  private void startSortie() {
    SortieModel sortieModel = new SortieModel();
    sortieModel.setDefault();

    sortieModel.setSortieName("sortie1");
//    sortieModel.setSortieUuid(Constant.TEST_SORTIE_ID);

    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.writeSortie(sortieModel, this, getContext());
  }

  /**
   * Read remote configuration...
   * ...then test authentication
   * ...then test location upload
   * ...then test observation upload
   * ...then test sortie upload
   */
  public void testChain() {
    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.readRemoteConfiguration(this, getContext());

    try {
      Thread.sleep(60 * 1000L);
    } catch(Exception exception) {
      //empty
    }

    assertTrue(authorizationFlag);
    assertTrue(configurationFlag);
    assertTrue(locationFlag);
    assertTrue(observationFlag);
    assertTrue(sortieFlag);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
