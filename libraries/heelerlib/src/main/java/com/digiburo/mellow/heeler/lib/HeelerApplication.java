package com.digiburo.mellow.heeler.lib;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Environment;

import com.digiburo.mellow.heeler.lib.service.SpeechService;
import com.digiburo.mellow.heeler.lib.utility.LegalMode;
import com.digiburo.mellow.heeler.lib.utility.PackageUtility;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.android.BasicLogcatConfigurator;

/**
 * @author gsc
 */
public class HeelerApplication extends Application {
  private static final Logger LOG = LoggerFactory.getLogger(HeelerApplication.class);

  /**
   * perform application startup chores);
   */
  @Override
  public void onCreate() {
    logConfiguration();
//    logTest();

    new UserPreferenceHelper(this);

    Personality.setApplicationVersion(PackageUtility.getAppVersion(this));
    Personality.setOperationMode(LegalMode.IDLE);

    testLocationProvider();

    startRoboSpice();
  }

  @Override
  public void onConfigurationChanged(Configuration configuration) {
    LOG.info("onConfigurationChanged");
    super.onConfigurationChanged(configuration);
  }

  @Override
  public void onLowMemory() {
    LOG.info("onLowMemory");
    super.onLowMemory();
  }

  @Override
  public void onTerminate() {
//    LOG.info("onTerminate");
    super.onTerminate();

    SpiceManager spiceManager = Personality.getSpiceManager();
    if (spiceManager.isStarted()) {
      try {
        spiceManager.shouldStop();
      } catch(Exception exception) {
        //empty
      }
    }
  }

  private void startRoboSpice() {
    SpiceManager spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);
    spiceManager.start(this);
    Personality.setSpiceManager(spiceManager);
  }

  private void testLocationProvider() {
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Personality.setGpsProvider(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
  }

  private void logConfiguration() {
    BasicLogcatConfigurator.configureDefaultContext();
  }

  private void logTest() {
    System.out.println("xoxoxoxoxoxoxo");
    LOG.trace("trace level message");
    LOG.debug("debug level message");
    LOG.info("info level message");
    LOG.warn("warn level message");
    LOG.error("error level message");
    System.out.println("xoxoxoxoxoxoxo");
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */
