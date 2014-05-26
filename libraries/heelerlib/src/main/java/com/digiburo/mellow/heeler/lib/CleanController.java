package com.digiburo.mellow.heeler.lib;

import android.content.Context;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationTable;
import com.digiburo.mellow.heeler.lib.database.ObservationTable;
import com.digiburo.mellow.heeler.lib.database.SortieTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class CleanController {
  private static final Logger LOG = LoggerFactory.getLogger(CleanController.class);

  /**
   * delete uploaded items from database
   * @param context
   */
  public void cleanDataBase(final Context context) {
    LOG.debug("database cleanup");

    DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
    dataBaseFacade.deleteUploaded(LocationTable.TABLE_NAME, LocationTable.Columns.UPLOAD_FLAG, context);
    dataBaseFacade.deleteUploaded(ObservationTable.TABLE_NAME, ObservationTable.Columns.UPLOAD_FLAG, context);
    dataBaseFacade.deleteUploaded(SortieTable.TABLE_NAME, SortieTable.Columns.UPLOAD_FLAG, context);

    System.out.println("tytytytytytyyt");

    Intent notifier = new Intent(Constant.CLEAN_EVENT);
    context.sendBroadcast(notifier);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */