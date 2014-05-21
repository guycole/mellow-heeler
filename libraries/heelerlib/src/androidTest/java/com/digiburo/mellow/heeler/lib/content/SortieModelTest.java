package com.digiburo.mellow.heeler.lib.content;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.TestHelper;

import junit.framework.TestCase;

/**
 * exercise sortie model
 * @author gsc
 */
public class SortieModelTest extends TestCase {
  private TestHelper testHelper = new TestHelper();

  public void testDefault() {
    SortieModel model = new SortieModel();
    assertTrue(SortieTable.TABLE_NAME.equals(model.getTableName()));
    assertTrue(SortieTable.CONTENT_URI.equals(model.getTableUri()));

    model.setDefault();

    assertEquals(0L, model.getId().longValue());
    assertTrue(Constant.DEFAULT_SORTIE_NAME.equals(model.getSortieName()));
    assertNotNull(model.getSortieUuid());
    assertEquals(36, model.getSortieUuid().length());
    assertFalse(model.isUploadFlag());
    assertTrue(model.getTimeStampMs() > 0);
    assertNotNull(model.getTimeStamp());
  }

  public void testId() {
    Long target = testHelper.randomLong();
    SortieModel model = new SortieModel();
    model.setId(target);
    assertEquals(target.longValue(), model.getId().longValue());
  }

  public void testSortieName() {
    String target = testHelper.randomString();
    SortieModel model = new SortieModel();
    model.setSortieName(target);
    assertTrue(target.equals(model.getSortieName()));
  }

  public void testSortieUuid() {
    String target = testHelper.randomString();
    SortieModel model = new SortieModel();
    model.setSortieUuid(target);
    assertTrue(target.equals(model.getSortieUuid()));
  }

  public void testUploadFlag() {
    SortieModel model = new SortieModel();
    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());
  }

  public void testTimeStamp() {
    long target = Math.abs(testHelper.randomLong());
    SortieModel model = new SortieModel();
    model.setTimeStampMs(target);
    assertEquals(target, model.getTimeStampMs());
    assertNotNull(model.getTimeStamp());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */