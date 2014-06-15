package com.digiburo.mellow.heeler.lib.network;

import com.digiburo.mellow.heeler.lib.Constant;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * RoboSpice Request
 * @author gsc
 */
public class ObservationRequest extends SpringAndroidSpiceRequest<ObservationResponse> {
  private static final Logger LOG = LoggerFactory.getLogger(ObservationRequest.class);

  private final String url;
  private final Observation observation;

  public ObservationRequest(final String url, final Observation observation) {
    super(ObservationResponse.class);

    this.url = url;
    this.observation = observation;
  }

  @Override
  public ObservationResponse loadDataFromNetwork() throws Exception {
    LOG.info(url);
    return getRestTemplate().postForObject(url, observation, ObservationResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */
