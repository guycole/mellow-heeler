package com.digiburo.mellow.heeler.lib.database;

import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.TestHelper;

import java.util.UUID;

/**
 * exercise hot table
 * @author gsc
 */
public class HotTableTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public HotTableTest() {
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
    HotModel original = new HotModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);
    assertEquals(rowKey.longValue(), original.getId().longValue());

    HotModel selected = dataBaseFacade.selectHot(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(original.equals(selected));

    HotModelList modelList = dataBaseFacade.selectAllHot(getContext());
    assertEquals(1, modelList.size());
  }

  public void testUpdate() {
    HotModel model = new HotModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    String bssid = testHelper.randomString();
    String ssid = testHelper.randomString();

    model.setBssid(bssid);
    model.setSsid(ssid);

    int count = dataBaseFacade.updateHot(model, getContext());
    assertEquals(1, count);

    HotModel selected = dataBaseFacade.selectHot(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());

    assertTrue(bssid.equals(selected.getBssid()));
    assertTrue(ssid.equals(selected.getSsid()));
  }

  public void testDelete() {
    HotModel original = new HotModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());

    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    int count = dataBaseFacade.deleteHot(rowKey, getContext());
    assertEquals(1, count);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 22, 2014 by gsc
 */
