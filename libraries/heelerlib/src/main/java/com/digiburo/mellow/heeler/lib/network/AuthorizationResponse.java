package com.digiburo.mellow.heeler.lib.network;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * discover if this installation is authorized to use remote server
 * @author gsc
 */

/*
{
  "receipt": "b6d1430d-b44f-4586bdc7-38c520f84a35",
  "status": "OK",
  "timeStamp": "2014-05-21T07:36:45Z",
  "messageVersion": 1,
  "remoteIpAddress": "127.0.0.1",
  "_links": {
    "self": {
      "href": "http://127.0.0.1:8080/ws/v1/authorize"
    }
  }
}
*/

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationResponse {
  private String receipt;
  private String remoteIpAddress;
  private String status;
  private String timeStamp;
  private Integer version;

  @JsonProperty("_links")
  private Links links;

  public Links getLinks() {
    return links;
  }

  public void setLinks(Links arg) {
    links = arg;
  }

  public String getReceipt() {
    return receipt;
  }

  public void setReceipt(String arg) {
    receipt = arg;
  }

  public String getRemoteIpAddress() {
    return remoteIpAddress;
  }

  public void setRemoteIpAddress(String arg) {
    remoteIpAddress = arg;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String arg) {
    status = arg;
  }

  public String getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer arg) {
    version = arg;
  }

  public static class Links {
    Self self;

    public Self getSelf() {
      return self;
    }

    public void setSelf(Self self) {
      this.self = self;
    }
  }

  public static class Self extends HrefContainer {
    //empty
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */