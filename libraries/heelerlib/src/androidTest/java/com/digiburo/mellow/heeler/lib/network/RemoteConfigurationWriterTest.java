package com.digiburo.mellow.heeler.lib.network;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.TestHelper;

/**
 * exercise remote configuration
 * @author gsc
 */
public class RemoteConfigurationWriterTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public RemoteConfigurationWriterTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
//    createApplication();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    System.out.println("xoxoxoxo ======== xoxoxoxox1");
    System.out.println("xoxoxoxo ======== xoxoxoxox1");
    System.out.println("xoxoxoxo ======== xoxoxoxox1");
    System.out.println("xoxoxoxo ======== xoxoxoxox1");
  }

  public void testOne() {
    RemoteConfigurationWriter remoteConfigurationWriter = new RemoteConfigurationWriter();
//    remoteConfiguration.doJsonGet(this, getContext());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
