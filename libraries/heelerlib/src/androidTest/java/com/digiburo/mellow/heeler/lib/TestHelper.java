package com.digiburo.mellow.heeler.lib;

import android.location.Location;
import android.location.LocationManager;

import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

import java.util.Date;
import java.util.Random;

/**
 * @author gsc
 */
public class TestHelper {
  private Random random = new Random();

  public Double randomDouble() {
    return random.nextDouble();
  }

  public Float randomFloat() {
    return random.nextFloat();
  }

  public Float randomPositiveFloat(float limit) {
    float result = random.nextFloat() * 100.0f;

    while ((result < 0.0) || (result > limit)) {
      result = random.nextFloat() * 100.0f;
    }

    return result;
  }

  public Integer randomInteger() {
    return random.nextInt();
  }

  public Float randomLatitude() {
    float result = random.nextFloat() * 100.0f;

    while (Math.abs(result) > 90.0) {
      result = random.nextFloat() * 100.0f;
    }

    return result;
  }

  public Float randomLongitude() {
    float result = random.nextFloat() * 100.0f;

    while (Math.abs(result) > 180.0) {
      result = random.nextFloat() * 100.0f;
    }

    return result;
  }

  public Long randomLong() {
    return random.nextLong();
  }

  public String randomString() {
    return Long.toHexString(random.nextLong());
  }

  public Location generateLocation() {
    Location location = new Location(LocationManager.PASSIVE_PROVIDER);
    location.setAccuracy(randomPositiveFloat(100.0f));
    location.setAltitude(randomPositiveFloat(10000.0f));
    location.setLatitude(randomLatitude());
    location.setLongitude(randomLongitude());

    Date date = new Date();
    location.setTime(date.getTime());

    return location;
  }

  public LocationModel generateLocationModel(final Location location, String sortieId) {
    LocationModel locationModel = new LocationModel();
    locationModel.setDefault();

    if (sortieId == null) {
      sortieId = locationModel.getSortieUuid();
    }

    if (location == null) {
      locationModel.setLocation(generateLocation(), sortieId);
    } else {
      locationModel.setLocation(location, sortieId);
    }

    return locationModel;
  }

  public ObservationModel generateObservationModel(String locationId, String sortieId) {
    int frequency = random.nextInt(6000);
    int level = random.nextInt(100);

    String bssid = randomString();
    String capability = randomString();
    String ssid = randomString();

    ObservationModel observationModel = new ObservationModel();
    observationModel.setDefault();

    if (locationId == null) {
      locationId = observationModel.getLocationUuid();
    }

    if (sortieId == null) {
      sortieId = observationModel.getSortieUuid();
    }

    observationModel.setScanResult(ssid, bssid, capability, frequency, level, locationId, sortieId);

    return observationModel;
  }

  public SortieModel generateSortieModel(String sortieId, String sortieName) {
    SortieModel sortieModel = new SortieModel();
    sortieModel.setDefault();

    if (sortieId != null) {
      sortieModel.setSortieUuid(sortieId);
    }

    if (sortieName != null) {
      sortieModel.setSortieName(sortieName);
    }

    return sortieModel;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 19, 2014 by gsc
 */
