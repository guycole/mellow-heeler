package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * write sortie rows to remote server
 * @author gsc
 */
public class SortieWriter {
  private static final Logger LOG = LoggerFactory.getLogger(SortieWriter.class);

  private Random random = new Random();

  /**
   * write sortie rows to remote server
   * @param sortieUuid
   * @param observationModelList
   * @param context
   */
  public void doJsonPost(final SortieModel sortieModel, final Context context) {
    LOG.debug("writer:" + sortieModel.getSortieUuid());

    SortieRequest request = new SortieRequest(sortieModel.getSortieUuid(), sortieModel.getSortieName(), sortieModel.getTimeStampMs(), context);
    String cacheKey = Integer.toString(random.nextInt());

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<SortieResponse>() {
      @Override
      public void onRequestFailure(SpiceException spiceException) {
        LOG.info("observation write failure");
      }

      @Override
      public void onRequestSuccess(SortieResponse sortieResponse) {
        LOG.info("sortie write success:" + sortieResponse.getStatus() + ":" + sortieResponse.getRemoteIpAddress());

        DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
        dataBaseFacade.setSortieUpload(sortieModel.getId(), context);
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */