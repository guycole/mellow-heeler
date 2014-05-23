package com.digiburo.mellow.heeler.lib.database;

import android.content.Context;
import android.test.ApplicationTestCase;

import com.digiburo.mellow.heeler.lib.HeelerApplication;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.TestHelper;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

/**
 * exercise sortie table
 * @author gsc
 */
public class SortieTableTest extends ApplicationTestCase<HeelerApplication> {
  private TestHelper testHelper = new TestHelper();

  public SortieTableTest() {
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
    SortieModel original = new SortieModel();
    original.setDefault();
    original.setSortieName(testHelper.randomString());

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);
    assertEquals(rowKey.longValue(), original.getId().longValue());

    SortieModel selected = dataBaseFacade.selectSortie(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(original.equals(selected));

    selected = dataBaseFacade.selectSortie(original.getSortieUuid(), getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(original.equals(selected));

    SortieModelList modelList = dataBaseFacade.selectAllSorties(true, getContext());
    assertEquals(1, modelList.size());

    modelList = dataBaseFacade.selectAllSorties(false, getContext());
    assertEquals(1, modelList.size());
  }

  public void testUpdate() {
    SortieModel model = new SortieModel();
    model.setDefault();
    model.setSortieName(testHelper.randomString());

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    long timeNow = TimeUtility.timeMillis();
    String freshName = testHelper.randomString();
    String freshUuid = testHelper.randomString();

    model.setTimeStampMs(timeNow);
    model.setSortieName(freshName);
    model.setSortieUuid(freshUuid);
    model.setUploadFlag();

    int count = dataBaseFacade.updateSortie(model, getContext());
    assertEquals(1, count);

    SortieModel selected = dataBaseFacade.selectSortie(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertEquals(timeNow, selected.getTimeStampMs());
    assertTrue(freshName.equals(selected.getSortieName()));
    assertTrue(freshUuid.equals(selected.getSortieUuid()));
    assertTrue(selected.isUploadFlag());
  }

  public void testDelete() {
    SortieModel original = new SortieModel();
    original.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());

    Long rowKey = dataBaseFacade.insert(original, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    int count = dataBaseFacade.deleteSortie(rowKey, getContext());
    assertEquals(1, count);
  }

  public void testUploadFlag() {
    SortieModel model = new SortieModel();
    model.setDefault();

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getContext());
    Long rowKey = dataBaseFacade.insert(model, getContext());
    assertNotNull(rowKey);
    assertTrue(rowKey.longValue() > 0);

    assertFalse(model.isUploadFlag());
    model.setUploadFlag();
    assertTrue(model.isUploadFlag());

    int count = dataBaseFacade.updateSortie(model, getContext());
    assertEquals(1, count);

    SortieModel selected = dataBaseFacade.selectSortie(rowKey, getContext());
    assertEquals(rowKey.longValue(), selected.getId().longValue());
    assertTrue(selected.isUploadFlag());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */
