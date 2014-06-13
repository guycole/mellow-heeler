package com.digiburo.mellow.heeler.lib;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.network.ConcreteListener;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.RemoteConfigurationResponse;

/**
 * @author gsc
 */
public class UploadLocationTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  private final NetworkFacade networkFacade = new NetworkFacade();

  private static int MAX_ROWS = 13;
  private volatile boolean configurationUpdated = false;

  public UploadLocationTest() {
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
  public void testUpload() {
//    prepareDataBase();

    UploadController uploadController = new UploadController();
    uploadController.uploadAll(getContext());

//    uploadController.freshGeoLocation();
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
   *
   * @return
   */
  private String prepareDataBase() {
    String sortieId = java.util.UUID.randomUUID().toString();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());

    for (int ii = 0; ii < MAX_ROWS; ii++) {
      dataBaseFacade.insert(testHelper.generateLocationModel(testHelper.generateLocation(), sortieId), getContext());
    }

    return sortieId;
  }
}