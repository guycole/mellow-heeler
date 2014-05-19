package com.digiburo.mellow.heeler.lib;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.content.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.network.GeoLocationWriter;
import com.digiburo.mellow.heeler.lib.network.ObservationWriter;
import com.digiburo.mellow.heeler.lib.utility.StringList;
import com.digiburo.mellow.heeler.lib.utility.NetworkUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class UploadController {
  private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

  /**
   * upload everything
   * @param context
   */
  public void uploadAll(Context context) {
    LOG.debug("uploadAll");

    if (NetworkUtility.isNetworkAvailable(context)) {
      LOG.debug("network available");
      uploadLocation(context);
      uploadObservation(context);
    } else {
      LOG.debug("network not available");
      return;
    }
  }

  /**
   * upload location
   * @param context
   */
  private void uploadLocation(Context context) {
    LOG.debug("uploadLocation");

    DataBaseFacade dataBaseFacade = new DataBaseFacade();
    StringList sorties = dataBaseFacade.selectLocationSorties(context);
    for (String sortie:sorties) {
      // each sortie is a separate upload
      LOG.debug("location upload for sortie:" + sortie);

      GeoLocationWriter geoLocationWriter = new GeoLocationWriter();
      geoLocationWriter.writer(context, sortie, dataBaseFacade.selectLocationsBySortie(context, sortie));
    }
  }

  /**
   * upload observations
   * @param context
   */
  private void uploadObservation(Context context) {
    LOG.debug("uploadObservation");

    DataBaseFacade dataBaseFacade = new DataBaseFacade();
    StringList sorties = dataBaseFacade.selectObservationSorties(context);
    for (String sortie:sorties) {
      // each sortie is a separate upload
      LOG.debug("location upload for sortie:" + sortie);

      ObservationWriter observationWriter = new ObservationWriter();
      observationWriter.writer(context, sortie, dataBaseFacade.selectObservationsBySortie(context, sortie));
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */