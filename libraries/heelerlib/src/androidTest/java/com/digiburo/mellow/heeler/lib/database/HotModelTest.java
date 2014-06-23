package com.digiburo.mellow.heeler.lib.database;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.TestHelper;

import junit.framework.TestCase;

import java.util.UUID;

/**
 * exercise hot model
 * @author gsc
 */
public class HotModelTest extends TestCase {
  private TestHelper testHelper = new TestHelper();

  public void testDefault() {
    HotModel model = new HotModel();
    assertTrue(HotTable.TABLE_NAME.equals(model.getTableName()));
    assertTrue(HotTable.CONTENT_URI.equals(model.getTableUri()));

    model.setDefault();

    assertEquals(0L, model.getId().longValue());

    assertTrue(Constant.UNKNOWN.equals(model.getSsid()));
    assertTrue(Constant.UNKNOWN.equals(model.getBssid()));
  }

  public void testId() {
    Long target = testHelper.randomLong();
    HotModel model = new HotModel();
    model.setId(target);
    assertEquals(target.longValue(), model.getId().longValue());
  }

  public void testHot() {
    String bssid = testHelper.randomString();
    String ssid = testHelper.randomString();

    HotModel model = new HotModel();
    model.setDefault();
    model.setBssid(bssid);
    model.setSsid(ssid);

    assertTrue(bssid.equals(model.getBssid()));
    assertTrue(ssid.equals(model.getSsid()));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 22, 2014 by gsc
 */