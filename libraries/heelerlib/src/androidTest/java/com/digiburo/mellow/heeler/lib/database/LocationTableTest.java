package com.digiburo.mellow.heeler.lib.database;

import android.location.Location;
import android.location.LocationManager;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import java.util.UUID;

/**
 * exercise location table
 * @author gsc
 */
public class LocationTableTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public LocationTableTest() {
    super(HeelerApplication.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    createApplication();
    Personality.setInternalDataBaseFileSystem(true);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testInsert() {
    LocationModel original = new LocationModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);
    assertEquals(rowKey.longValue(), original.getId().longValue());

    LocationModel selected = dataBaseFacade.selectLocation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(original.equals(selected));

    LocationModelList modelList = dataBaseFacade.selectAllLocations(true, original.getSortieUuid(), getContext());
    assertEquals(1, modelList.size());

    modelList = dataBaseFacade.selectAllLocations(false, original.getSortieUuid(), getContext());
    assertEquals(1, modelList.size());
  }

  public void testUpdate() {
    LocationModel model = new LocationModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    Location location = new Location(LocationManager.PASSIVE_PROVIDER);
    location.setAccuracy(testHelper.randomFloat());
    location.setAltitude(testHelper.randomDouble());
    location.setLatitude(testHelper.randomDouble());
    location.setLongitude(testHelper.randomDouble());
    location.setTime(TimeUtility.timeMillis());

    String sortieId = UUID.randomUUID().toString();

    model.setLocation(location, sortieId);

    int count = dataBaseFacade.updateLocation(model, getContext());
    assertEquals(1, count);

    LocationModel selected = dataBaseFacade.selectLocation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertEquals(location.getAccuracy(), selected.getAccuracy().doubleValue(), Constant.EPSILON);
    assertEquals(location.getAltitude(), selected.getAltitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getLatitude(), selected.getLatitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getLongitude(), selected.getLongitude().doubleValue(), Constant.EPSILON);
    assertEquals(location.getTime(), selected.getTimeStampMs());

    assertNotNull(selected.getTimeStamp());
    assertTrue(sortieId.equals(selected.getSortieUuid()));
  }

  public void testDelete() {
    LocationModel original = new LocationModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());

    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    int count = dataBaseFacade.deleteLocation(rowKey, getContext());
    assertEquals(1, count);
  }

  public void testSpecialFlag() {
    LocationModel model = new LocationModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    assertFalse(model.isSpecialFlag());
    model.setSpecialFlag();
    assertTrue(model.isSpecialFlag());

    int count = dataBaseFacade.updateLocation(model, getContext());
    assertEquals(1, count);

    LocationModel selected = dataBaseFacade.selectLocation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(selected.isSpecialFlag());
  }

  public void testUploadFlag() {
    LocationModel model = new LocationModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());

    int count = dataBaseFacade.updateLocation(model, getContext());
    assertEquals(1, count);

    LocationModel selected = dataBaseFacade.selectLocation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(selected.isUploadFlag());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
