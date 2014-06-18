package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.service.UploadService;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * @param sortieModel
   * @param context
   */
  public void doJsonPost(final SortieModel sortieModel, final Class handlerClass, final Context context) {
    LOG.debug("writer:" + sortieModel.getSortieUuid());

    SortieRequest request = new SortieRequest(sortieModel.getSortieUuid(), sortieModel.getSortieName(), sortieModel.getTimeStampMs(), context);
    String cacheKey = Integer.toString(random.nextInt());

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<SortieResponse>() {
      @Override
      public void onRequestFailure(SpiceException spiceException) {
        LOG.info("observation write failure");

        Intent notifier = new Intent(context, UploadService.class);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.SORTIE.toString());
        notifier.putExtra(Constant.INTENT_STATUS_FLAG, false);
        context.startService(notifier);
      }

      @Override
      public void onRequestSuccess(SortieResponse sortieResponse) {
        LOG.info("sortie write success:" + sortieResponse.getRemoteIpAddress() + ":" + sortieResponse.getStatus());

        if (!Constant.OK.equals(sortieResponse.getStatus())) {
          LOG.error("bad remote status:" + sortieResponse.getStatus());
        }

        if (!sortieModel.getSortieUuid().equals(sortieResponse.getSortieId())) {
          LOG.error("bad remote sortie UUID");
        }

        if (sortieResponse.getRowCount() != 1) {
          LOG.error("bad remote row count:" + sortieResponse.getRowCount());
        }

        DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
        dataBaseFacade.setSortieUpload(sortieModel.getId(), context);

        Intent notifier = new Intent(context, UploadService.class);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.SORTIE.toString());
        notifier.putExtra(Constant.INTENT_STATUS_FLAG, true);
        context.startService(notifier);
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */