package com.digiburo.mellow.heeler.lib.network;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * container for JSON remote configuration
 * @author gsc
 */

/*
{
  "_links": {
    "self": { "href": "http://digiburo.com/hal/mellow-heeler.json" },
    "authorize": { "href": "https://mellow-heeler.appspot.com/ws/v1/authorize" },
    "location": { "href": "https://mellow-heeler.appspot.com/ws/v1/location" },
    "observation": { "href": "https://mellow-heeler.appspot.com/ws/v1/observation" },
    "sortie": { "href": "https://mellow-heeler.appspot.com/ws/v1/sortie" }
  },
  "messageVersion":1,
  "revisionDate":"2014-05-19T02:18:51Z"
}
*/

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteConfigurationResponse {
  private Integer version;
  private String revisionDate;

  @JsonProperty("_links")
  private Links links;

  public Links getLinks() {
    return links;
  }

  public void setLinks(Links arg) {
    links = arg;
  }

  public Integer getMessageVersion() {
    return version;
  }

  public void setMessageVersion(Integer version) {
    this.version = version;
  }

  public String getRevisionDate() {
    return revisionDate;
  }

  public void setRevisionDate(String revisionDate) {
    this.revisionDate = revisionDate;
  }

  public static class Links {
    Self self;
    Authorize authorize;
    Location location;
    Observation observation;
    Sortie sortie;

    public Self getSelf() {
      return self;
    }

    public void setSelf(Self self) {
      this.self = self;
    }

    public Authorize getAuthorize() {
      return authorize;
    }

    public void setAuthorize(Authorize authorize) {
      this.authorize = authorize;
    }

    public Location getLocation() {
      return location;
    }

    public void setLocation(Location location) {
      this.location = location;
    }

    public Observation getObservation() {
      return observation;
    }

    public void setObservation(Observation observation) {
      this.observation = observation;
    }

    public Sortie getSortie() {
      return sortie;
    }

    public void setSortie(Sortie sortie) {
      this.sortie = sortie;
    }
  }

  public static class Self extends HrefContainer {
    //empty
  }

  public static class Authorize extends HrefContainer {
    //empty
  }

  public static class Location extends HrefContainer {
    //empty
  }

  public static class Observation extends HrefContainer {
    //empty
  }

  public static class Sortie extends HrefContainer {
    //empty
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 20, 2014 by gsc
 */