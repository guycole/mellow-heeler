package com.digiburo.mellow.heeler.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.service.UploadService;

/**
 * FIXME - there is a trick to using broadcast receiver from unit test
 * http://stackoverflow.com/questions/5769315/unit-testing-a-broadcast-receiver
 * @author gsc
 */
public class UploadServiceTestOld extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  private final NetworkFacade networkFacade = new NetworkFacade();

  private boolean authFlag = false;
  private boolean uploadFlag = false;

  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.hasExtra(Constant.INTENT_AUTH_FLAG)) {
        authFlag = intent.getBooleanExtra(Constant.INTENT_AUTH_FLAG, false);
      } else if (intent.hasExtra(Constant.INTENT_UPLOAD_FLAG)) {
        uploadFlag = intent.getBooleanExtra(Constant.INTENT_UPLOAD_FLAG, false);
      }
    }
  };

  public UploadServiceTestOld() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createApplication();
    Personality.setInternalDataBaseFileSystem(true);
    Personality.setRemoteConfigurationUrl(Constant.TEST_CONFIGURATION_URL);
//    getContext().registerReceiver(broadcastReceiver, new IntentFilter(Constant.UPLOAD_EVENT));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
//    getContext().unregisterReceiver(broadcastReceiver);
  }

  public void testRemoteConfiguration() {
    prepareDataBase();
    getContext().startService(new Intent(getContext(), UploadService.class));

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = UploadService.MAX_COUNT + 1;
      }
    } while ((testCount < UploadService.MAX_COUNT) && (!authFlag));

    assertFalse(authFlag);
    assertFalse(uploadFlag);
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