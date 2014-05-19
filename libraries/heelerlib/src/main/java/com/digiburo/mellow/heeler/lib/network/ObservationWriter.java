package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.content.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.content.LocationModel;
import com.digiburo.mellow.heeler.lib.content.ObservationModel;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * write observation rows to remote server
 * @author gsc
 */
public class ObservationWriter {
  private static final Logger LOG = LoggerFactory.getLogger(ObservationWriter.class);

  /**
   * write observation rows to remote server
   * @param context
   * @param sortieUuid
   * @param observationModelList
   */
  public void writer(final Context context, final String sortieUuid, final List<ObservationModel> observationModelList) {
    LOG.debug("writer:" + observationModelList.size() + ":" + sortieUuid);

    if (observationModelList.isEmpty()) {
      return;
    }

    ArrayList<ObservationElement> observationList = new ArrayList<ObservationElement>();
    for (ObservationModel observationModel:observationModelList) {
      ObservationElement observationElement = new ObservationElement();
      observationElement.setBssid(observationModel.getBssid());
      observationElement.setSsid(observationModel.getSsid());
      observationElement.setCapability(observationModel.getCapability());
      observationElement.setFrequency(observationModel.getFrequency());
      observationElement.setStrength(observationModel.getLevel());
      observationElement.setTimeStampMs(observationModel.getTimeStampMs());
      observationElement.setLocationId(observationModel.getLocationUuid());
      observationList.add(observationElement);
    }

    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    String installationUuid = userPreferenceHelper.getInstallationId(context);
    String locationUrl = userPreferenceHelper.getLocationUrl(context);
    //TODO get URL from json configuration
    locationUrl = "https://mellow-heeler.appspot.com/ws/v1/observation";

    Observation observation = new Observation();
    observation.setInstallationId(installationUuid);
    observation.setMessageVersion(1);
    observation.setSortieId(sortieUuid);
    observation.setObservationList(observationList);

    ObservationRequest request = new ObservationRequest(locationUrl, observation);
    String cacheKey = TimeUtility.timeNow().toString();

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ObservationResponse>() {
      @Override
      public void onRequestFailure(SpiceException spiceException) {
        LOG.info("observation write failure");
      }

      @Override
      public void onRequestSuccess(ObservationResponse observationResponse) {
        LOG.info("observation write success:" + observationResponse.getStatus() + ":" + observationResponse.getRemoteIpAddress());

        /*
        DataBaseFacade dataBaseFacade = new DataBaseFacade();
        for(LocationModel locationModel:locationModelList) {
          dataBaseFacade.setLocationUpload(locationModel.getId(), context);
        }
        */
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */