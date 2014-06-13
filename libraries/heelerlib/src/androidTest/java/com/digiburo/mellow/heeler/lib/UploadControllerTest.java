package com.digiburo.mellow.heeler.lib;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.network.AbstractListener;
import com.digiburo.mellow.heeler.lib.network.ConcreteListener;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.RemoteConfigurationResponse;

/**
 * @author gsc
 */
public class UploadControllerTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  private final NetworkFacade networkFacade = new NetworkFacade();

  private volatile boolean configurationUpdated = false;

  public UploadControllerTest() {
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

  //////////////

  public void testRemoteConfiguration() {
 //   AbstractListener abstractListener = new AbstractListener(getContext());
 //   abstractListener.readRemoteConfiguration(this, getContext());
  }

  /**
   * ensure there are rows to upload
   */
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
}