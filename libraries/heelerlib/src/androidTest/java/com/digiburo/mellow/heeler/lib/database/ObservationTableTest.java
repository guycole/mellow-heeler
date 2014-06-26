package com.digiburo.mellow.heeler.lib.database;

import android.net.wifi.ScanResult;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.TestHelper;

import java.util.UUID;

/**
 * exercise observation table
 * @author gsc
 */
public class ObservationTableTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public ObservationTableTest() {
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
    ObservationModel original = new ObservationModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);
    assertEquals(rowKey.longValue(), original.getId().longValue());

    ObservationModel selected = dataBaseFacade.selectObservation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(original.equals(selected));

    ObservationModel selected2 = dataBaseFacade.selectObservation(selected.getObservationUuid(), getContext());
    assertEquals(rowKey.longValue(), selected2.getId().longValue());

    ObservationModelList modelList = dataBaseFacade.selectAllObservations(true, original.getSortieUuid(), 0, getContext());
    assertEquals(1, modelList.size());

    modelList = dataBaseFacade.selectAllObservations(false, original.getSortieUuid(), 0, getContext());
    assertEquals(1, modelList.size());

    modelList = dataBaseFacade.selectBssidObservations(selected.getBssid(), getContext());
    assertEquals(1, modelList.size());

    modelList = dataBaseFacade.selectDistinctBssid(getContext());
    assertEquals(1, modelList.size());
  }

  public void testUpdate() {
    ObservationModel model = new ObservationModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    int level = testHelper.randomInteger();
    int frequency = testHelper.randomInteger();

    String capability = testHelper.randomString();
    String bssid = testHelper.randomString();
    String ssid = testHelper.randomString();

    String locationId = UUID.randomUUID().toString();
    String sortieId = UUID.randomUUID().toString();

    model.setScanResult(ssid, bssid, capability, frequency, level, locationId, sortieId);
    model.setUploadFlag();

    int count = dataBaseFacade.updateObservation(model, getContext());
    assertEquals(1, count);

    ObservationModel selected = dataBaseFacade.selectObservation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());

    assertEquals(level, selected.getLevel());
    assertEquals(frequency, selected.getFrequency());
    assertTrue(capability.equals(selected.getCapability()));
    assertTrue(bssid.equals(selected.getBssid()));
    assertTrue(ssid.equals(selected.getSsid()));

    assertTrue(locationId.equals(selected.getLocationUuid()));
    assertTrue(sortieId.equals(selected.getSortieUuid()));

    assertTrue(selected.isUploadFlag());
  }

  public void testDelete() {
    ObservationModel original = new ObservationModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());

    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    int count = dataBaseFacade.deleteObservation(rowKey, getContext());
    assertEquals(1, count);
  }

  public void testUploadFlag() {
    ObservationModel model = new ObservationModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());

    int count = dataBaseFacade.updateObservation(model, getContext());
    assertEquals(1, count);

    ObservationModel selected = dataBaseFacade.selectObservation(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(selected.isUploadFlag());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
