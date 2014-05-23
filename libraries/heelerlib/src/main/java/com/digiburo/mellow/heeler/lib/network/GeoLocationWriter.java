package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * write location rows to remote server
 * @author gsc
 */
public class GeoLocationWriter {
  private static final Logger LOG = LoggerFactory.getLogger(GeoLocationWriter.class);

  private Random random = new Random();

  /**
   * write location rows to remote server
   * @param context
   * @param sortieUuid
   * @param locationModelList
   */
  public void doJsonPost(final String sortieUuid, final List<LocationModel> locationModelList, final Context context) {
    LOG.debug("doJsonPost:" + locationModelList.size() + ":" + sortieUuid);

    if (locationModelList.isEmpty()) {
      return;
    }

    ArrayList<GeoLocationElement> locationList = new ArrayList<GeoLocationElement>();
    for(LocationModel locationModel:locationModelList) {
      GeoLocationElement geoLocationElement = new GeoLocationElement();
      geoLocationElement.setAccuracy(locationModel.getAccuracy());
      geoLocationElement.setAltitude(locationModel.getAltitude());
      geoLocationElement.setLatitude(locationModel.getLatitude());
      geoLocationElement.setLongitude(locationModel.getLongitude());
      geoLocationElement.setLocationId(locationModel.getLocationUuid());
      geoLocationElement.setSpecialFlag(locationModel.isSpecialFlag());
      geoLocationElement.setTimeStampMs(locationModel.getTimeStampMs());
      locationList.add(geoLocationElement);
    }

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    String installationUuid = userPreferenceHelper.getInstallationId(context);
    String locationUrl = userPreferenceHelper.getLocationUrl(context);

    final GeoLocation geoLocation = new GeoLocation();
    geoLocation.setInstallationId(installationUuid);
    geoLocation.setSortieId(sortieUuid);
    geoLocation.setLocationList(locationList);

    GeoLocationRequest request = new GeoLocationRequest(locationUrl, geoLocation);
    String cacheKey = Integer.toString(random.nextInt());

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<GeoLocationResponse>() {
      @Override
      public void onRequestFailure(SpiceException spiceException) {
        LOG.info("geoloc write failure");
      }

      @Override
      public void onRequestSuccess(GeoLocationResponse geoLocationResponse) {
        LOG.info("geoloc write success:" + geoLocationResponse.getStatus() + ":" + geoLocationResponse.getRemoteIpAddress());
        LOG.info("geoloc write success:" + geoLocationResponse.getRowCount() + ":" + geoLocationResponse.getSortieId());

        DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
        for (LocationModel locationModel:locationModelList) {
          dataBaseFacade.setLocationUpload(locationModel.getId(), context);
        }
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */