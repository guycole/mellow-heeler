package com.digiburo.mellow.heeler.lib.network;

import com.digiburo.mellow.heeler.lib.Personality;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote configuration is obtained from a remote server.
 * Configuration rarely changes.  Read prior to uploading logs.
 * @author gsc
 */
public class RemoteConfigurationRequest extends SpringAndroidSpiceRequest<RemoteConfigurationResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(RemoteConfigurationRequest.class);

  // provisioning URL
  public static final String URL = Personality.getRemoteConfigurationUrl();

  public RemoteConfigurationRequest() {
    super(RemoteConfigurationResponse.class);
  }

  @Override
  public RemoteConfigurationResponse loadDataFromNetwork() throws Exception {
    return getRestTemplate().getForObject(URL, RemoteConfigurationResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */