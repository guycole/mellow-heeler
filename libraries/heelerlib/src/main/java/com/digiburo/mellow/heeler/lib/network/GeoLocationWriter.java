package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.service.UploadService;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;
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
   * @param sortieUuid
   * @param locationModelList
   * @param context
   */
  public void doJsonPost(final String sortieUuid, final List<LocationModel> locationModelList, final Class handlerClass, final Context context) {
    LOG.debug("doJsonPost:" + locationModelList.size() + ":" + sortieUuid);

    if (locationModelList.isEmpty()) {
      return;
    }

    final ArrayList<GeoLocationElement> locationList = new ArrayList<GeoLocationElement>();
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
    if (locationUrl.contains(Constant.TEST_URL_FRAGMENT)) {
      installationUuid = Constant.TEST_INSTALLATION_ID;
    }

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

        Intent notifier = new Intent(context, handlerClass);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.GEOLOCATION.toString());
        notifier.putExtra(Constant.INTENT_STATUS_FLAG, false);
        context.startService(notifier);
      }

      @Override
      public void onRequestSuccess(GeoLocationResponse geoLocationResponse) {
        LOG.info("geoloc write success:" + geoLocationResponse.getRemoteIpAddress() + ":" + geoLocationResponse.getStatus());

        if (!Constant.OK.equals(geoLocationResponse.getStatus())) {
          LOG.error("bad remote status:" + geoLocationResponse.getStatus());
        }

        if (!sortieUuid.equals(geoLocationResponse.getSortieId())) {
          LOG.error("bad remote sortie UUID");
        }

        int rowCount = geoLocationResponse.getRowCount();
        if (rowCount != locationModelList.size()) {
          LOG.error("bad remote row count:" + rowCount + ":" + locationModelList.size());
        }

        Intent notifier = new Intent(context, handlerClass);
        notifier.putExtra(Constant.INTENT_JSON_TYPE, LegalJsonMessage.GEOLOCATION.toString());
        notifier.putExtra(Constant.INTENT_STATUS_FLAG, true);
        context.startService(notifier);
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */