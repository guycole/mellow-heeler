package com.digiburo.mellow.heeler.lib.network;

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

  private Observation observation;
  private String url;

  public ObservationRequest(String url, Observation observation) {
    super(ObservationResponse.class);

    this.url = url;
    this.observation = observation;
  }

  @Override
  public ObservationResponse loadDataFromNetwork() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return getRestTemplate().postForObject(url, observation, ObservationResponse.class);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */
