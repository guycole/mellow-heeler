package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.content.LocationModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author gsc
 */
public class NetworkFacade implements NetworkListener {
  private static final Logger LOG = LoggerFactory.getLogger(NetworkFacade.class);

  /**
   * Remote service configuration is obtained from a remote server.
   * Configuration rarely changes.  Should be read prior to uploading collected datum.
   * If the configuration version has changed, then load fresh values into user preferences.
   * @param context
   */
  public void readRemoteConfiguration(final Context context) {
    RemoteConfiguration remoteConfiguration = new RemoteConfiguration();
    remoteConfiguration.readRemoteConfiguration(context);
  }

//  public void writeSorties(final Context, final String sortieUuid, final List<LocationModel> locationModelList))

}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */