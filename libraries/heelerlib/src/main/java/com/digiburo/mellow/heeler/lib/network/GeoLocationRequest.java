package com.digiburo.mellow.heeler.lib.network;

import com.digiburo.mellow.heeler.lib.Constant;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * RoboSpice Request
 * @author gsc
 */
public class GeoLocationRequest extends SpringAndroidSpiceRequest<GeoLocationResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(GeoLocationRequest.class);

  private final String url;
  private final GeoLocation geoLocation;

  public GeoLocationRequest(final String url, final GeoLocation geoLocation) {
    super(GeoLocationResponse.class);

    this.url = url;
    this.geoLocation = geoLocation;
  }

  @Override
  public GeoLocationResponse loadDataFromNetwork() throws Exception {
    return getRestTemplate().postForObject(url, geoLocation, GeoLocationResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */
