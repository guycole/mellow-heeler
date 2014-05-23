package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Personality;
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
  public void doJsonPost(final Context context) {
    LOG.debug("test authorization");

    AuthorizationRequest request = new AuthorizationRequest(context);
    String cacheKey = TimeUtility.timeNow().toString();

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<AuthorizationResponse>() {
      @Override
      public void onRequestFailure(final SpiceException spiceException) {
        LOG.info("authorize failure");
      }

      @Override
      public void onRequestSuccess(final AuthorizationResponse authorizationResponse) {
        LOG.debug("authorize success:" + authorizationResponse.getStatus());
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */