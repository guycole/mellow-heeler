package com.digiburo.mellow.heeler.lib.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;

public class ConfigurationTest extends ServiceTestCase<NetworkFacadeService> {
  public static final int MAX_COUNT = 12;

  private TestHelper testHelper = new TestHelper();

  public ConfigurationTest() {
    super(NetworkFacadeService.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testBasicServiceCreate() {
    Intent intent = new Intent(getSystemContext(), NetworkFacadeService.class);
//    intent.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.AUTHORIZATION.toString());
//    intent.putExtra(Constant.INTENT_STATUS_FLAG, true);

    startService(intent);
    NetworkFacadeService service = getService();
    assertTrue(service.created);

//   assertEquals(0, service.startId);
//    assertTrue(service.lastJsonStatus);
//    assertEquals(LegalJsonMessage.CONFIGURATION, service.messageType);

  }

  public void testConfiguration() {
    startService(new Intent(getSystemContext(), NetworkFacadeService.class));
    NetworkFacadeService service = getService();

    NetworkFacade networkFacade = new NetworkFacade();
    networkFacade.readRemoteConfiguration(NetworkFacadeService.class, getContext());

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (service.messageType == LegalJsonMessage.UNKNOWN));

//    assertTrue(service.lastJsonStatus);
//    assertEquals(LegalJsonMessage.CONFIGURATION, service.messageType);
  }
}