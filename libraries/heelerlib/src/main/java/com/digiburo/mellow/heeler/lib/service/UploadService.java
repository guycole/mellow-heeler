package com.digiburo.mellow.heeler.lib.service;

import android.app.IntentService;
import android.content.Intent;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.digiburo.mellow.heeler.lib.network.AuthorizationResponse;
import com.digiburo.mellow.heeler.lib.network.GeoLocationResponse;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.NetworkListener;
import com.digiburo.mellow.heeler.lib.network.ObservationResponse;
import com.digiburo.mellow.heeler.lib.network.RemoteConfigurationResponse;
import com.digiburo.mellow.heeler.lib.network.SortieResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * discover items within database which require uploading and write them to remote server
 */
public class UploadService extends IntentService implements NetworkListener {
  public static final int MAX_ROWS = 5;
  public static final int MAX_COUNT = 12;

  private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

  private enum ResponseOption {WAITING, FAILURE, SUCCESS};
  private ResponseOption authorizationResponseOption = ResponseOption.WAITING;
  private ResponseOption configurationResponseOption = ResponseOption.WAITING;
  private ResponseOption locationResponseOption = ResponseOption.WAITING;
  private ResponseOption observationResponseOption = ResponseOption.WAITING;
  private ResponseOption sortieResponseOption = ResponseOption.WAITING;

  private enum StateOption {CONFIGURATION, AUTHORIZE, LOCATION, OBSERVATION, SORTIE, DONE};
  private StateOption runOption = StateOption.CONFIGURATION;

  private DataBaseFacade dataBaseFacade;
  private final NetworkFacade networkFacade = new NetworkFacade();

  /**
   * ensure remote server will accept upload
   *
   * @param authorizationResponse (or null if error)
   */
  public void freshAuthorization(final AuthorizationResponse authorizationResponse) {
    LOG.debug("fresh authorization noted");

    if (authorizationResponse == null) {
      authorizationResponseOption = ResponseOption.FAILURE;
    } else {
      if (Constant.OK.equals(authorizationResponse.getStatus())) {
        authorizationResponseOption = ResponseOption.SUCCESS;

        Intent notifier = new Intent(Constant.UPLOAD_EVENT);
        notifier.putExtra(Constant.INTENT_AUTH_FLAG, true);
        sendBroadcast(notifier);
      } else {
        authorizationResponseOption = ResponseOption.FAILURE;

        Intent notifier = new Intent(Constant.UPLOAD_EVENT);
        notifier.putExtra(Constant.INTENT_AUTH_FLAG, false);
        sendBroadcast(notifier);
      }
    }
  }

  /**
   * success location write
   *
   * @param geoLocationResponse (or null if error)
   */
  public void freshGeoLocation(final GeoLocationResponse geoLocationResponse) {
    LOG.debug("fresh geolocation noted");

    if (geoLocationResponse == null) {
      locationResponseOption = ResponseOption.FAILURE;
    } else {
      if (Constant.OK.equals(geoLocationResponse.getStatus())) {
        locationResponseOption = ResponseOption.SUCCESS;
      } else {
        locationResponseOption = ResponseOption.FAILURE;
      }
    }
  }

  /**
   * success observation write
   *
   * @param observationResponse (or null if error)
   */
  public void freshObservation(final ObservationResponse observationResponse) {
    LOG.debug("fresh observation noted");

    if (observationResponse == null) {
      observationResponseOption = ResponseOption.FAILURE;
    } else {
      if (Constant.OK.equals(observationResponse.getStatus())) {
        observationResponseOption = ResponseOption.SUCCESS;
      } else {
        observationResponseOption = ResponseOption.FAILURE;
      }
    }
  }

  /**
   * refresh remote configuration
   *
   * @param remoteConfigurationResponse (or null if error)
   */
  public void freshRemoteConfiguration(final RemoteConfigurationResponse remoteConfigurationResponse) {
    LOG.debug("fresh configuration noted");

    if (remoteConfigurationResponse == null) {
      configurationResponseOption = ResponseOption.FAILURE;
    } else {
      configurationResponseOption = ResponseOption.SUCCESS;
    }
  }

  /**
   * success sortie write
   *
   * @param sortieResponse (or null if error)
   */
  public void freshSortie(final SortieResponse sortieResponse) {
    LOG.debug("fresh sortie noted");

    if (sortieResponse == null) {
      sortieResponseOption = ResponseOption.FAILURE;
    } else {
      if (Constant.OK.equals(sortieResponse.getStatus())) {
        sortieResponseOption = ResponseOption.SUCCESS;
      } else {
        sortieResponseOption = ResponseOption.FAILURE;
      }
    }
  }

  public UploadService() {
    super("UploadService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    LOG.debug("xxx xxx onHandleIntent xxx xxx ");

    dataBaseFacade = new DataBaseFacade(this);
    SortieModelList sortieModelList = dataBaseFacade.selectAllSorties(false, this);

    for (SortieModel sortieModel : sortieModelList) {
      runOption = StateOption.CONFIGURATION;

      do {
        LOG.debug("switch:" + runOption + ":" + sortieModel.getSortieName() + ":" + sortieModel.getSortieUuid());

        switch (runOption) {
          case AUTHORIZE:
            runOption = testAuthorize();
            break;
          case CONFIGURATION:
            runOption = readConfiguration();
            break;
          case LOCATION:
            runOption = writeLocation(sortieModel);
            break;
          case OBSERVATION:
            runOption = writeObservation(sortieModel);
            break;
          case SORTIE:
            runOption = writeSortie(sortieModel);
            break;
        }
      } while (runOption != StateOption.DONE);
    }

    Intent notifier = new Intent(Constant.UPLOAD_EVENT);
    notifier.putExtra(Constant.INTENT_UPLOAD_FLAG, true);
    sendBroadcast(notifier);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("xxx xxx onDestroy xxx xxx ");
  }

  private StateOption readConfiguration() {
    LOG.debug("xxx xxx readConfiguration xxx xxx ");
    networkFacade.readRemoteConfiguration(this, this);

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (configurationResponseOption == ResponseOption.WAITING));

    if (configurationResponseOption == ResponseOption.SUCCESS) {
      LOG.info("configuration success noted, return authorize");
      return StateOption.AUTHORIZE;
    }

    LOG.info("configuration failure noted, return done");
    return StateOption.DONE;
  }

  private StateOption testAuthorize() {
    LOG.debug("xxx xxx testAuthorize xxx xxx ");
    networkFacade.testAuthorization(this, this);

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
 //       System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (authorizationResponseOption == ResponseOption.WAITING));

    if (authorizationResponseOption == ResponseOption.SUCCESS) {
      LOG.info("authorization success noted, return location");
      return StateOption.LOCATION;
    }

    LOG.info("authorization failure noted, return done");
    return StateOption.DONE;
  }

  private StateOption writeLocation(final SortieModel sortieModel) {
    LOG.debug("xxx xxx writeLocation xxx xxx ");

    String sortieUuid = sortieModel.getSortieUuid();

    LocationModelList locationModelList = new LocationModelList();
    LocationModelList tempList = dataBaseFacade.selectAllLocations(false, sortieUuid, this);
    if (tempList.isEmpty()) {
      return StateOption.OBSERVATION;
    } else if (tempList.size() < MAX_ROWS) {
      locationModelList = tempList;
    } else {
      for (int ii = 0; ii < MAX_ROWS; ii++) {
        locationModelList.add(tempList.get(ii));
      }
    }

    //write to remote server
    networkFacade.writeLocations(sortieUuid, locationModelList, this, this);

    //wait for remote server
    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (locationResponseOption == ResponseOption.WAITING));

    if (locationResponseOption == ResponseOption.SUCCESS) {
      for (LocationModel locationModel:locationModelList) {
        //mark upload success
        dataBaseFacade.setLocationUpload(locationModel.getId(), this);
      }

      LOG.info("location success noted, make another pass");
      return StateOption.LOCATION;
    }

    LOG.info("location failure noted, return done");
    return StateOption.DONE;
  }

  private StateOption writeObservation(final SortieModel sortieModel) {
    LOG.debug("xxx xxx writeObservation xxx xxx ");

    String sortieUuid = sortieModel.getSortieUuid();

    ObservationModelList observationModelList = new ObservationModelList();
    ObservationModelList tempList = dataBaseFacade.selectAllObservations(false, sortieUuid, this);
    if (tempList.isEmpty()) {
      return StateOption.SORTIE;
    } else if (tempList.size() < MAX_ROWS) {
      observationModelList = tempList;
    } else {
      for (int ii = 0; ii < MAX_ROWS; ii++) {
        observationModelList.add(tempList.get(ii));
      }
    }

    //write to remote server
    networkFacade.writeObservations(sortieUuid, observationModelList, this, this);

    //wait for remote server
    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (observationResponseOption == ResponseOption.WAITING));

    if (observationResponseOption == ResponseOption.SUCCESS) {
      for (ObservationModel observationModel:observationModelList) {
        //mark upload success
        dataBaseFacade.setObservationUpload(observationModel.getId(), this);
      }

      LOG.info("observation success noted, make another pass");
      return StateOption.OBSERVATION;
    }

    LOG.info("observation failure noted, return done");
    return StateOption.DONE;
  }

  private StateOption writeSortie(final SortieModel sortieModel) {
    LOG.debug("xxx xxx writeSortie xxx xxx ");
    networkFacade.writeSortie(sortieModel, this, this);

    int testCount = 0;

    do {
      try {
        ++testCount;
        Thread.sleep(3 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = MAX_COUNT + 1;
      }
    } while ((testCount < MAX_COUNT) && (sortieResponseOption == ResponseOption.WAITING));

    if (authorizationResponseOption == ResponseOption.SUCCESS) {
      dataBaseFacade.setSortieUpload(sortieModel.getId(), this);

      LOG.info("sortie success noted, return done");
      return StateOption.DONE;
    }

    LOG.info("sortie failure noted, return done");
    return StateOption.DONE;
  }
}