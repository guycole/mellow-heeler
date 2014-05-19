package com.digiburo.mellow.heeler.lib;

import java.util.Date;
import java.util.UUID;

/**
 * sortie container
 * @author gsc
 */
public class Sortie {
  private UUID sortieId = UUID.randomUUID();
  private Date startTimeStamp = new Date();
  private Date stopTimeStamp;
  private String note = "No Note";

  public UUID getSortieId() {
    return sortieId;
  }

  public void setSortieId(UUID taskId) {
    this.sortieId = taskId;
  }

  public Date getStartTimeStamp() {
    return startTimeStamp;
  }

  public void setStartTimeStamp(Date startTimeStamp) {
    this.startTimeStamp = startTimeStamp;
  }

  public Date getStopTimeStamp() {
    return stopTimeStamp;
  }

  public void setStopTimeStamp(Date stopTimeStamp) {
    this.stopTimeStamp = stopTimeStamp;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}

/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
