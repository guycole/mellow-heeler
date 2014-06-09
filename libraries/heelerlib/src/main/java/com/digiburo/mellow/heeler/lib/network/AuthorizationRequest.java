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
 * discover if this installation is authorized to use remote server
 * @author gsc
 */
public class AuthorizationRequest extends SpringAndroidSpiceRequest<AuthorizationResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationRequest.class);

  private final String url;
  private final Map<String, String> parameters = new HashMap<String, String>();

  public AuthorizationRequest(final Context context) {
    super(AuthorizationResponse.class);

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    url = userPreferenceHelper.getAuthorizeUrl(context);

    if (url.contains(Constant.TEST_URL_FRAGMENT)) {
      parameters.put("installationId", Constant.TEST_INSTALLATION_ID);
    } else {
      parameters.put("installationId", userPreferenceHelper.getInstallationId(context));
    }
  }

  @Override
  public AuthorizationResponse loadDataFromNetwork() throws Exception {
    return getRestTemplate().postForObject(url, parameters, AuthorizationResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */