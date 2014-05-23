package com.digiburo.mellow.heeler.lib;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import com.digiburo.mellow.heeler.lib.database.DataBaseHelper;
import com.digiburo.mellow.heeler.lib.service.LocationService;
import com.digiburo.mellow.heeler.lib.utility.PackageUtility;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
    logTest();

    new UserPreferenceHelper(this);

    Personality.setApplicationVersion(PackageUtility.getAppVersion(this));

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
    LOG.info("onTerminate");
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
