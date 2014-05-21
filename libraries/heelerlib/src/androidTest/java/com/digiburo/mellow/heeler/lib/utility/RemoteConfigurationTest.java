package com.digiburo.mellow.heeler.lib.utility;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.network.RemoteConfiguration;

/**
 * @author gsc
 */
public class RemoteConfigurationTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public RemoteConfigurationTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createApplication();
  }

  public void test01() {
    RemoteConfiguration remoteConfiguration = new RemoteConfiguration();
// E/AndroidRuntime(25812): android.app.RemoteServiceException: Bad notification for startForeground: java.lang.RuntimeException: icon must be non-zero
//    remoteConfiguration.readRemoteConfiguration(getContext());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
