package com.digiburo.mellow.heeler.lib.content;

import android.location.Location;
import android.location.LocationManager;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import junit.framework.TestCase;

import java.util.UUID;

/**
 * exercise location model
 * @author gsc
 */
public class LocationModelTest extends TestCase {
  private TestHelper testHelper = new TestHelper();

  public void testDefault() {
    LocationModel model = new LocationModel();
    assertTrue(LocationTable.TABLE_NAME.equals(model.getTableName()));
    assertTrue(LocationTable.CONTENT_URI.equals(model.getTableUri()));

    model.setDefault();

    assertEquals(0L, model.getId().longValue());
    assertFalse(model.isUploadFlag());

    assertNotNull(model.getLocationUuid());
    assertEquals(36, model.getLocationUuid().length());
    assertNotNull(model.getSortieUuid());
    assertEquals(36, model.getSortieUuid().length());

    assertTrue(model.getTimeStampMs() > 0);
    assertNotNull(model.getTimeStamp());

    assertEquals(-1.0, model.getAccuracy().doubleValue(), Constant.EPSILON);
    assertEquals(0.0, model.getAltitude().doubleValue(), Constant.EPSILON);
    assertEquals(0.0, model.getLatitude().doubleValue(), Constant.EPSILON);
    assertEquals(0.0, model.getLongitude().doubleValue(), Constant.EPSILON);
  }

  public void testId() {
    Long target = testHelper.randomLong();
    LocationModel model = new LocationModel();
    model.setId(target);
    assertEquals(target.longValue(), model.getId().longValue());
  }

  public void testLocation() {
    Location location = new Location(LocationManager.PASSIVE_PROVIDER);
    location.setAccuracy(testHelper.randomFloat());
    location.setAltitude(testHelper.randomDouble());
    location.setLatitude(testHelper.randomDouble());
    location.setLongitude(testHelper.randomDouble());
    location.setTime(TimeUtility.timeMillis());

    String sortieId = UUID.randomUUID().toString();

    LocationModel model = new LocationModel();
    model.setDefault();
    model.setLocation(location, sortieId);

    assertEquals(location.getAccuracy(), model.getAccuracy().doubleValue(), Constant.EPSILON);
    assertEquals(location.getAltitude(), model.getAltitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getLatitude(), model.getLatitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getLongitude(), model.getLongitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getTime(), model.getTimeStampMs());

    assertNotNull(model.getTimeStamp());
    assertTrue(sortieId.equals(model.getSortieUuid()));
  }

  public void testUploadFlag() {
    LocationModel model = new LocationModel();
    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */