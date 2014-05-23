package com.digiburo.mellow.heeler.lib.network;

import android.content.Context;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
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
import java.util.Random;

/**
 * write observation rows to remote server
 * @author gsc
 */
public class ObservationWriter {
  private static final Logger LOG = LoggerFactory.getLogger(ObservationWriter.class);

  private Random random = new Random();

  /**
   * write observation rows to remote server
   * @param sortieUuid
   * @param observationModelList
   * @param networkListener
   * @param context
   */
  public void doJsonPost(final String sortieUuid, final List<ObservationModel> observationModelList, final NetworkListener networkListener, final Context context) {
    LOG.debug("writer:" + observationModelList.size() + ":" + sortieUuid);

    if (observationModelList.isEmpty()) {
      return;
    }

    final ArrayList<ObservationElement> observationList = new ArrayList<ObservationElement>();
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
    String observationUrl = userPreferenceHelper.getObservationUrl(context);

    Observation observation = new Observation();
    observation.setInstallationId(installationUuid);
    observation.setMessageVersion(1);
    observation.setSortieId(sortieUuid);
    observation.setObservationList(observationList);

    ObservationRequest request = new ObservationRequest(observationUrl, observation);
    String cacheKey = Integer.toString(random.nextInt());

    SpiceManager spiceManager = Personality.getSpiceManager();
    spiceManager.execute(request, cacheKey, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ObservationResponse>() {
      @Override
      public void onRequestFailure(SpiceException spiceException) {
        LOG.info("observation write failure");
      }

      @Override
      public void onRequestSuccess(ObservationResponse observationResponse) {
        LOG.info("observation write success:" + observationResponse.getRemoteIpAddress() + ":" + observationResponse.getVersion() + ":" + observationResponse.getStatus());

        if (!Constant.OK.equals(observationResponse.getStatus())) {
          LOG.error("bad remote status:" + observationResponse.getStatus());
        }

        if (!sortieUuid.equals(observationResponse.getSortieId())) {
          LOG.error("bad remote sortie UUID");
        }

        int rowCount = observationResponse.getRowCount();
        if (rowCount != observationList.size()) {
          LOG.error("bad remote row count:" + rowCount + ":" + observationList.size());
        }

        DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
        for (ObservationModel observationModel:observationModelList) {
          dataBaseFacade.setObservationUpload(observationModel.getId(), context);
        }

        if (networkListener == null) {
          LOG.debug("skipping listener");
        } else {
          LOG.debug("invoking listener");
          networkListener.freshObservation(observationResponse);
        }
      }
    });
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 17, 2014 by gsc
 */