package com.digiburo.mellow.heeler.lib.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;

/**
 * @author gsc
 */


/* 
 * Copyright 2014 Digital Burro, INC
 * Created 5/19/14 by gsc
 */

public class UserPreferenceHelperTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public UserPreferenceHelperTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    new UserPreferenceHelper(getContext());
  }

  public void testInstallationId() {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    // install UUID automatically generated
    assertNotNull(userPreferenceHelper.getInstallationId(getContext()));
    assertEquals(36, userPreferenceHelper.getInstallationId(getContext()).length());
  }

  public void testAudioCue() {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setAudioCue(getContext(), false);
    assertFalse(userPreferenceHelper.isAudioCue(getContext()));
    userPreferenceHelper.setAudioCue(getContext(), true);
    assertTrue(userPreferenceHelper.isAudioCue(getContext()));
  }

  public void testAuthorizeUrl() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setAuthorizeUrl(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getAuthorizeUrl(getContext())));
  }

  public void testLocationUrl() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setLocationUrl(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getLocationUrl(getContext())));
  }

  public void testObservationUrl() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setObservationUrl(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getObservationUrl(getContext())));
  }

  public void testPollFrequency() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setPollFrequency(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getPollFrequency(getContext())));
  }

  public void testWebServiceConfigVersion() {
    Integer temp = testHelper.randomInteger();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setWebServiceConfigVersion(getContext(), temp);
    assertEquals(temp, userPreferenceHelper.getWebServiceConfigVersion(getContext()));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
