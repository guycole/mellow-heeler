package com.digiburo.mellow.heeler.lib.service;

import android.content.Intent;
import android.test.ServiceTestCase;
import com.digiburo.mellow.heeler.lib.TestHelper;

public class UploadServiceTest extends ServiceTestCase<UploadService> {
  private TestHelper testHelper = new TestHelper();

  public UploadServiceTest() {
    super(UploadService.class);
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
    Intent intent = new Intent(getSystemContext(), UploadService.class);
//    intent.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.AUTHORIZATION.toString());
//    intent.putExtra(Constant.INTENT_STATUS_FLAG, true);

    startService(intent);
    UploadService service = getService();
    assertTrue(service.created);
//   assertEquals(0, service.startId);
//    assertTrue(service.lastJsonStatus);
//    assertEquals(LegalJsonMessage.CONFIGURATION, service.messageType);

  }
}