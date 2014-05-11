package com.digiburo.mellow.heeler.lib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.digiburo.mellow.heeler.lib.Personality;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * collect GPS location
 * @author gsc
 */
public class LocationService extends Service implements LocationListener {
  private static final Logger LOG = LoggerFactory.getLogger(LocationService.class);

  public void onLocationChanged(Location location) {
    LOG.debug("xxx xxx Location Change:" + location.toString());
    Personality.setCurrentLocation(location);
  }

  public void onProviderDisabled(String provider) {
    LOG.debug("xxx xxx provider disabled:" + provider);
  }

  public void onProviderEnabled(String provider) {
    LOG.debug("xxx xxx provider enabled:" + provider);
  }

  public void onStatusChanged(String provider, int status, Bundle extras) {
    LOG.debug("xxx xxx onStatusChanged:" + provider + ":" + status + ":" + extras.toString());

    if (extras.isEmpty()) {
      LOG.debug("xxx xxx empty bundle xxx xxx");
    } else {
      LOG.debug("xxx xxx bundle size:" + extras.size());

      Set<String> keys = extras.keySet();
      Iterator<String> ii = keys.iterator();
      while (ii.hasNext()) {
        String key = ii.next();
        LOG.debug("xxx xxx extra key:" + key);
      }

      LOG.debug("xxx xxx population:" + extras.getInt("satellites"));
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    LOG.debug("xxx xxx onCreate xxx xxx ");

    int oneKm = 1000;
    long timeOut = 60 * 1000L;

    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeOut, oneKm, this);

    Personality.setCurrentLocation(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("xxx xxx onDestroy xxx xxx ");
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationManager.removeUpdates(this);
  }

  @Override
  public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
