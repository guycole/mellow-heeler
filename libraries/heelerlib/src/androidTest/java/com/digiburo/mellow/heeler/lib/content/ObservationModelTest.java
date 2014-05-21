package com.digiburo.mellow.heeler.lib.content;

import android.location.Location;
import android.location.LocationManager;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import junit.framework.TestCase;

import java.util.UUID;

/**
 * exercise observation model
 * @author gsc
 */
public class ObservationModelTest extends TestCase {
  private TestHelper testHelper = new TestHelper();

  public void testDefault() {
    ObservationModel model = new ObservationModel();
    assertTrue(ObservationTable.TABLE_NAME.equals(model.getTableName()));
    assertTrue(ObservationTable.CONTENT_URI.equals(model.getTableUri()));

    model.setDefault();

    assertEquals(0L, model.getId().longValue());
    assertFalse(model.isUploadFlag());

    assertNotNull(model.getLocationUuid());
    assertEquals(36, model.getLocationUuid().length());
    assertNotNull(model.getSortieUuid());
    assertEquals(36, model.getSortieUuid().length());

    assertTrue(model.getTimeStampMs() > 0);
    assertNotNull(model.getTimeStamp());

    assertEquals(0, model.getFrequency());
    assertEquals(0, model.getLevel());

    assertTrue(Constant.UNKNOWN.equals(model.getSsid()));
    assertTrue(Constant.UNKNOWN.equals(model.getBssid()));
    assertTrue(Constant.UNKNOWN.equals(model.getCapability()));
  }

  public void testId() {
    Long target = testHelper.randomLong();
    ObservationModel model = new ObservationModel();
    model.setId(target);
    assertEquals(target.longValue(), model.getId().longValue());
  }

  public void testObservation() {
    int level = testHelper.randomInteger();
    int frequency = testHelper.randomInteger();

    String capability = testHelper.randomString();
    String bssid = testHelper.randomString();
    String ssid = testHelper.randomString();

    String locationId = UUID.randomUUID().toString();
    String sortieId = UUID.randomUUID().toString();

    ObservationModel model = new ObservationModel();
    model.setDefault();
    model.setScanResult(ssid, bssid, capability, frequency, level, locationId, sortieId);

    assertEquals(level, model.getLevel());
    assertEquals(frequency, model.getFrequency());
    assertTrue(capability.equals(model.getCapability()));
    assertTrue(bssid.equals(model.getBssid()));
    assertTrue(ssid.equals(model.getSsid()));

    assertTrue(locationId.equals(model.getLocationUuid()));
    assertTrue(sortieId.equals(model.getSortieUuid()));

    assertNotNull(model.getTimeStamp());
    assertTrue(locationId.equals(model.getLocationUuid()));
    assertTrue(sortieId.equals(model.getSortieUuid()));
  }

  public void testUploadFlag() {
    ObservationModel model = new ObservationModel();
    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */