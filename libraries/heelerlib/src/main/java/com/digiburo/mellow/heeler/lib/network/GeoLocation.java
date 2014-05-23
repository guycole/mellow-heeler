package com.digiburo.mellow.heeler.lib.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * container for JSON location POST
 * @author gsc
 */
public class GeoLocation {
  private String installationId;
  private Integer messageVersion = 1;
  private String sortieId;
  private List<GeoLocationElement> locationList;

  public String getInstallationId() {
    return installationId;
  }

  public void setInstallationId(String installationId) {
    this.installationId = installationId;
  }

  public Integer getMessageVersion() {
    return messageVersion;
  }

  public void setMessageVersion(Integer messageVersion) {
    this.messageVersion = messageVersion;
  }

  public String getSortieId() {
    return sortieId;
  }

  public void setSortieId(String sortieId) {
    this.sortieId = sortieId;
  }

  public List<GeoLocationElement> getLocationList() {
    return locationList;
  }

  public void setLocationList(List<GeoLocationElement> locationList) {
    this.locationList = locationList;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */