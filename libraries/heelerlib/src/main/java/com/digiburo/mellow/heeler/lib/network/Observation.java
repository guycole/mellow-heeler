package com.digiburo.mellow.heeler.lib.network;

import java.util.List;

/**
 * container for JSON observation POST
 * @author gsc
 */
public class Observation {
  private String installationId;
  private String sortieId;
  private List<ObservationElement> observationList;

  public String getInstallationId() {
    return installationId;
  }

  public void setInstallationId(String installationId) {
    this.installationId = installationId;
  }

  public String getSortieId() {
    return sortieId;
  }

  public void setSortieId(String sortieId) {
    this.sortieId = sortieId;
  }

  public List<ObservationElement> getObservationList() {
    return observationList;
  }

  public void setObservationList(List<ObservationElement> observationList) {
    this.observationList = observationList;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */