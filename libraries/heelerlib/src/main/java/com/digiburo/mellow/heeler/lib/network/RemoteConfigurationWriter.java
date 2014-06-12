package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * read remote configuration
 * @author gsc
 */
public class RemoteConfigurationWriter {
  private static final Logger LOG = LoggerFactory.getLogger(RemoteConfigurationWriter.class);

  /**
   *
   * @param remoteConfigurationResponse
   */
  private void loadResponse(final RemoteConfigurationResponse remoteConfigurationResponse, final Context context) {
    UserPreferenceHelper uph = new UserPreferenceHelper(context);
    int knownVersion = uph.getRemoteConfigurationVersion(context);
    int freshVersion = remoteConfigurationResponse.getMessageVersion().intValue();
    if (freshVersion == knownVersion) {
      LOG.debug("skipping update, same version:" + knownVersion);
    } else {
      LOG.debug("must update remote configuration to version:" + freshVersion);
      uph.setAuthorizeUrl(context, remoteConfigurationResponse.getLinks().getAuthorize().getHref());
      uph.setLocationUrl(context, remoteConfigurationResponse.getLinks().getLocation().getHref());
      uph.setObservationUrl(context, remoteConfigurationResponse.getLinks().getObservation().getHref());
      uph.setSortieUrl(context, remoteConfigurationResponse.getLinks().getSortie().getHref());
      uph.setRemoteConfigurationVersion(context, freshVersion);
    }
  }

  /**
   * Remote service configuration is obtained from a remote server.
   * Configuration rarely changes.  Should be read prior to uploading collected datum.
   * If the configuration version has changed, then load fresh values into user preferences.
   * @param networkListener
   * @param context
   */
  public void doJsonGet(final NetworkListener networkListener, final Context context) {
    LOG.debug("remote configuration read");

    RemoteConfigurationRequest request = new RemoteConfigurationRequest();
    String cacheKey = TimeUtility.timeNow().toString();

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<RemoteConfigurationResponse>() {
      @Override
      public void onRequestFailure(final SpiceException spiceException) {
        LOG.info("request failure");
      }

      @Override
      public void onRequestSuccess(final RemoteConfigurationResponse remoteConfigurationResponse) {
        LOG.info("request success");

        loadResponse(remoteConfigurationResponse, context);

        if (networkListener == null) {
          LOG.info("skipping listener");
        } else {
          LOG.info("invoking listener");
          networkListener.freshRemoteConfiguration(remoteConfigurationResponse);
        }
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */