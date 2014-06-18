package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.service.UploadService;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * test for authorization
 * @author gsc
 */
public class AuthorizationWriter {
  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationWriter.class);

  /**
   * discover if this installation is authorized to use remote server
   * @param context
   */
  public void doJsonPost(final Class handlerClass, final Context context) {
    LOG.debug("test authorization");

    AuthorizationRequest request = new AuthorizationRequest(context);
    String cacheKey = TimeUtility.timeNow().toString();

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<AuthorizationResponse>() {
      @Override
      public void onRequestFailure(final SpiceException spiceException) {
        LOG.info("authorize failure:" + spiceException);

        Intent notifier = new Intent(context, handlerClass);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.AUTHORIZATION.toString());
        notifier.putExtra(Constant.INTENT_STATUS_FLAG, false);
        context.startService(notifier);
      }

      @Override
      public void onRequestSuccess(final AuthorizationResponse authorizationResponse) {
        LOG.debug("authorize success:" + authorizationResponse.getRemoteIpAddress() + ":" + authorizationResponse.getStatus());

        Intent notifier = new Intent(context, handlerClass);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.AUTHORIZATION.toString());

        if (Constant.OK.equals(authorizationResponse.getStatus())) {
          notifier.putExtra(Constant.INTENT_STATUS_FLAG, true);
        } else {
          notifier.putExtra(Constant.INTENT_STATUS_FLAG, false);
          LOG.error("bad remote status:" + authorizationResponse.getStatus());
        }

        context.startService(notifier);
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */