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
//  private final String url = Constant.DIAGNOSTIC_URL;
  private final Map<String, String> parameters = new HashMap<String, String>();

  public AuthorizationRequest(final Context context) {
    super(AuthorizationResponse.class);

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    parameters.put("installationId", userPreferenceHelper.getInstallationId(context));
    parameters.put("messageVersion", "1");

    url = userPreferenceHelper.getAuthorizeUrl(context);
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