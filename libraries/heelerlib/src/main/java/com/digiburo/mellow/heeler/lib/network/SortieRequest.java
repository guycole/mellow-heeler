package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * RoboSpice Request
 * @author gsc
 */
public class SortieRequest extends SpringAndroidSpiceRequest<SortieResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(SortieRequest.class);

  private final String url;
  private final Map<String, String> parameters = new HashMap<String, String>();

  /**
   * RoboSpice Request
   * @param sortieId
   * @param sortieName
   * @param timeStampMs
   * @param context
   */
  public SortieRequest(final String sortieId, final String sortieName, final Long timeStampMs, final Context context) {
    super(SortieResponse.class);

    parameters.put("sortieId", sortieId);
    parameters.put("sortieName", sortieName);
    parameters.put("timeStampMs", Long.toString(timeStampMs));

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    url = userPreferenceHelper.getSortieUrl(context);
    if (url.contains(Constant.TEST_URL_FRAGMENT)) {
      parameters.put("installationId", Constant.TEST_INSTALLATION_ID);
    } else {
      parameters.put("installationId", userPreferenceHelper.getInstallationId(context));
    }
  }

  /**
   *
   * @return
   * @throws Exception
   */
  @Override
  public SortieResponse loadDataFromNetwork() throws Exception {
    LOG.info(url);
    return getRestTemplate().postForObject(url, parameters, SortieResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 23, 2014 by gsc
 */
