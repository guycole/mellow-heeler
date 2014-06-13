package com.digiburo.mellow.heeler.lib.network;

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

/**
 * Exercise network listener - write random models to remote server
 * @author gsc
 */
public class ConcreteListenerTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  private final NetworkFacade networkFacade = new NetworkFacade();

  private volatile boolean configurationUpdated = false;

  public ConcreteListenerTest() {
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

  public void testAuthentication() {
    RemoteConfigurationResponse remoteConfigurationResponse = waitForConfiguration();
    assertNotNull(remoteConfigurationResponse);

    ConcreteListener concreteListener = new ConcreteListener(getContext());
    networkFacade.testAuthorization(concreteListener, getContext());

    int testCount = 0;
    AuthorizationResponse authorizationResponse = null;

    do {
      authorizationResponse = concreteListener.getAuthorizationResponse();
      if (authorizationResponse == null) {
        ++testCount;

        try {
          Thread.sleep(5 * 1000L);
        } catch(Exception exception) {
          //empty
        }
      }
    } while ((testCount < 12) && (authorizationResponse == null));

    assertNotNull(authorizationResponse);
    assertTrue(Constant.OK.equals(authorizationResponse.getStatus()));
  }

  public void testLocation() {
    RemoteConfigurationResponse remoteConfigurationResponse = waitForConfiguration();
    assertNotNull(remoteConfigurationResponse);

    String sortieId = java.util.UUID.randomUUID().toString();

    LocationModel model1 = testHelper.generateLocationModel(testHelper.generateLocation(), sortieId);
    LocationModel model2 = testHelper.generateLocationModel(testHelper.generateLocation(), sortieId);

    LocationModelList locationModelList = new LocationModelList();
    locationModelList.add(model1);
    locationModelList.add(model2);

    ConcreteListener concreteListener = new ConcreteListener(getContext());
    networkFacade.writeLocations(sortieId, locationModelList, concreteListener, getContext());

    int testCount = 0;
    GeoLocationResponse geoLocationResponse = null;

    do {
      geoLocationResponse = concreteListener.getGeoLocationResponse();
      if (geoLocationResponse == null) {
        ++testCount;

        try {
          Thread.sleep(5 * 1000L);
        } catch(Exception exception) {
          //empty
        }
      }
    } while ((testCount < 12) && (geoLocationResponse == null));

    assertNotNull(geoLocationResponse);
    assertTrue(Constant.OK.equals(geoLocationResponse.getStatus()));
    assertEquals(2, geoLocationResponse.getRowCount().intValue());
  }

  public void testObservation() {
    RemoteConfigurationResponse remoteConfigurationResponse = waitForConfiguration();
    assertNotNull(remoteConfigurationResponse);

    String locationId = java.util.UUID.randomUUID().toString();
    String sortieId = java.util.UUID.randomUUID().toString();

    ObservationModel model1 = testHelper.generateObservationModel(locationId, sortieId);
    ObservationModel model2 = testHelper.generateObservationModel(locationId, sortieId);

    ObservationModelList observationModelList = new ObservationModelList();
    observationModelList.add(model1);
    observationModelList.add(model2);

    ConcreteListener concreteListener = new ConcreteListener(getContext());
    networkFacade.writeObservations(sortieId, observationModelList, concreteListener, getContext());

    int testCount = 0;
    ObservationResponse observationResponse = null;

    do {
      observationResponse = concreteListener.getObservationResponse();
      if (observationResponse == null) {
        ++testCount;

        try {
          Thread.sleep(5 * 1000L);
        } catch(Exception exception) {
          //empty
        }
      }
    } while ((testCount < 12) && (observationResponse == null));

    assertNotNull(observationResponse);
    assertTrue(Constant.OK.equals(observationResponse.getStatus()));
    assertEquals(2, observationResponse.getRowCount().intValue());
  }

  public void testRemoteConfiguration() {
    RemoteConfigurationResponse remoteConfigurationResponse = waitForConfiguration();
    assertNotNull(remoteConfigurationResponse);

    System.out.println("zxzx:" + remoteConfigurationResponse);
    System.out.println("zxzx:" + remoteConfigurationResponse.getLinks());
    System.out.println("zxzx:" + remoteConfigurationResponse.getLinks().getSelf());
    System.out.println("zxzx:" + remoteConfigurationResponse.getLinks().getSelf().getHref());

    assertTrue(Constant.TEST_CONFIGURATION_URL.equals(remoteConfigurationResponse.getLinks().getSelf().getHref()));

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    assertTrue(userPreferenceHelper.getAuthorizeUrl(getContext()).equals(remoteConfigurationResponse.getLinks().getAuthorize().getHref()));
    assertTrue(userPreferenceHelper.getLocationUrl(getContext()).equals(remoteConfigurationResponse.getLinks().getLocation().getHref()));
    assertTrue(userPreferenceHelper.getObservationUrl(getContext()).equals(remoteConfigurationResponse.getLinks().getObservation().getHref()));
    assertTrue(userPreferenceHelper.getSortieUrl(getContext()).equals(remoteConfigurationResponse.getLinks().getSortie().getHref()));

    configurationUpdated = true;
  }

  public void testSortie() {
    RemoteConfigurationResponse remoteConfigurationResponse = waitForConfiguration();
    assertNotNull(remoteConfigurationResponse);

    String sortieId = java.util.UUID.randomUUID().toString();
    String sortieName = testHelper.randomString();

    SortieModel sortieModel = testHelper.generateSortieModel(sortieId, sortieName);

    ConcreteListener concreteListener = new ConcreteListener(getContext());
    networkFacade.writeSortie(sortieModel, concreteListener, getContext());

    int testCount = 0;
    SortieResponse sortieResponse = null;

    do {
      sortieResponse = concreteListener.getSortieResponse();
      if (sortieResponse == null) {
        ++testCount;

        try {
          Thread.sleep(5 * 1000L);
        } catch(Exception exception) {
          //empty
        }
      }
    } while ((testCount < 12) && (sortieResponse == null));

    assertNotNull(sortieResponse);
    assertTrue(Constant.OK.equals(sortieResponse.getStatus()));
    assertEquals(1, sortieResponse.getRowCount().intValue());
  }

  /**
   *
   */
  private RemoteConfigurationResponse waitForConfiguration() {
    ConcreteListener concreteListener = new ConcreteListener(getContext());
    networkFacade.readRemoteConfiguration(concreteListener, getContext());

    int testCount = 0;
    RemoteConfigurationResponse remoteConfigurationResponse = null;

    do {
      remoteConfigurationResponse = concreteListener.getRemoteConfigurationResponse();
      if (remoteConfigurationResponse == null) {
        ++testCount;

        try {
          Thread.sleep(5 * 1000L);
        } catch(Exception exception) {
          //empty
        }
      }
    } while ((testCount < 12) && (remoteConfigurationResponse == null));

    return remoteConfigurationResponse;
  }

  /**
   * ensure there are rows to upload
   */
  /*
  private void prepareDataBase() {
    SortieModel sortieModel = testHelper.generateSortieModel(null, "uploadTest");
    LocationModel locationModel1 = testHelper.generateLocationModel(null, sortieModel.getSortieUuid());
    LocationModel locationModel2 = testHelper.generateLocationModel(null, sortieModel.getSortieUuid());
    ObservationModel observationModel1 = testHelper.generateObservationModel(locationModel1.getLocationUuid(), sortieModel.getSortieUuid());
    ObservationModel observationModel2 = testHelper.generateObservationModel(locationModel2.getLocationUuid(), sortieModel.getSortieUuid());

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    dataBaseFacade.insert(sortieModel, getContext());
    dataBaseFacade.insert(locationModel1, getContext());
    dataBaseFacade.insert(locationModel2, getContext());
    dataBaseFacade.insert(observationModel1, getContext());
    dataBaseFacade.insert(observationModel2, getContext());
  }
  */
}