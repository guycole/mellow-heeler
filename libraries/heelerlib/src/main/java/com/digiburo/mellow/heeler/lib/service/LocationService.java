package com.digiburo.mellow.heeler.lib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.utility.AudioHelper;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * collect locations
 * @author gsc
 */
public class LocationService extends Service implements LocationListener {
  private static final Logger LOG = LoggerFactory.getLogger(LocationService.class);

  public static void startLocationService(Context context) {
    context.startService(new Intent(context, LocationService.class));
  }

  public static void stopLocationService(Context context) {
    context.stopService(new Intent(context, LocationService.class));
  }

  public void onLocationChanged(Location location) {
    LOG.debug("xxx xxx Location Change:" + location.toString());
    freshLocation(location);
  }

  public void onProviderDisabled(String provider) {
    LOG.debug("xxx xxx provider disabled:" + provider);
    //TODO error dialog, must have location services
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

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(this);
    int distance = Integer.parseInt(userPreferenceHelper.getPollDistance(this));

    //timeout usually ignored by platform, but I optimistically supply it anyway
    long timeOut = 30 * 1000L;

    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeOut, distance, this);

    freshLocation(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
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

  /**
   * service a fresh location
   */
  private void freshLocation(final Location location) {
    Thread thread = new Thread(new FreshLocation(location, Personality.getCurrentSortie().getSortieUuid(), this));
    thread.start();
  }

  class FreshLocation implements Runnable {
    private final Location location;
    private final String sortieId;
    private final Context context;

    public FreshLocation(final Location location, final String sortieId, final Context context) {
      this.location = location;
      this.sortieId = sortieId;
      this.context = context;
    }

    @Override
    public void run() {
      if (location == null) {
        LOG.error("ignoring null location");
        return;
      }

      if (sortieId == null) {
        LOG.debug("no sortie - ignoring location update");
        return;
      }

      LocationModel locationModel = new LocationModel();
      locationModel.setDefault();
      locationModel.setLocation(location, sortieId);

      DataBaseFacade dataBaseFacade = new DataBaseFacade(getBaseContext());
      dataBaseFacade.insert(locationModel, getBaseContext());

      Personality.setCurrentLocation(locationModel);

      sendBroadcast(new Intent(Constant.FRESH_UPDATE));

      AudioHelper audioHelper = new AudioHelper();
      audioHelper.notifier(context);
    }
  }
}

/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
