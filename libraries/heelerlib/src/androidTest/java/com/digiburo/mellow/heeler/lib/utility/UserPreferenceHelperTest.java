package com.digiburo.mellow.heeler.lib.utility;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.TestHelper;

/**
 * @author gsc
 */
public class UserPreferenceHelperTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public UserPreferenceHelperTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createApplication();
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

  public void testSortieUrl() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setSortieUrl(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getSortieUrl(getContext())));
  }

  public void testSpeechCue() {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setSpeechCue(getContext(), false);
    assertFalse(userPreferenceHelper.isSpeechCue(getContext()));
    userPreferenceHelper.setSpeechCue(getContext(), true);
    assertTrue(userPreferenceHelper.isSpeechCue(getContext()));
  }

  public void testPollDistance() {
    String temp = testHelper.randomString();
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(getContext());
    userPreferenceHelper.setPollDistance(getContext(), temp);
    assertTrue(temp.equals(userPreferenceHelper.getPollDistance(getContext())));
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
    userPreferenceHelper.setRemoteConfigurationVersion(getContext(), temp);
    assertEquals(temp, userPreferenceHelper.getRemoteConfigurationVersion(getContext()));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
