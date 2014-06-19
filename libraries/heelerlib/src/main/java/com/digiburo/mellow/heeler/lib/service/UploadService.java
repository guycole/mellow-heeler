package com.digiburo.mellow.heeler.lib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * discover items within database which require uploading and write them to remote server
 *
 * <OL>
 *   <LI>Read configuration for URL</LI>
 *   <LI>Test remote server authentication and ensure uploads will be accepted</LI>
 *   <LI>Discover sorties which require uploading</LI>
 *   <LI>Select locations for sortie, upload in groups of MAX_ROWS</LI>
 *   <LI>Mark uploaded locations within database, select next group</LI>
 *   <LI>When all locations for sortie have been uploaded, then upload observations</LI>
 *   <LI>When all observations have been uploaded, select next sortie and repeat</LI>
 * </OL>
 *
 * TODO: move away from main thread
 */
public class UploadService extends Service {
  public static final int MAX_ROWS = 30;
  public static final int MAX_COUNT = 12;

  //testing kludges
  public boolean created = false;
  public int startId = 0;
  public boolean lastJsonStatus = false;
  public LegalJsonMessage messageType = LegalJsonMessage.UNKNOWN;

  private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

  private DataBaseFacade dataBaseFacade;
  private final NetworkFacade networkFacade = new NetworkFacade();

  private LocationModelList locationModelList;
  private ObservationModelList observationModelList;

  private int sortieNdx;
  private SortieModelList sortieModelList;

  /**
   *
   * @param context
   */
  public static void startUploadService(Context context) {
    Intent intent = new Intent(context, UploadService.class);
    context.startService(intent);
  }

  /**
   * mandatory empty ctor
   */
  public UploadService() {
    //empty
  }

  @Override
  public void onCreate() {
    super.onCreate();
    created = true;
    LOG.debug("xxx xxx onCreate xxx xxx");
    dataBaseFacade = new DataBaseFacade(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("xxx xxx onDestroy xxx xxx");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    LOG.debug("xxx xxx onStart xxx xxx");
    this.startId = startId;

    boolean statusFlag = intent.getBooleanExtra(Constant.INTENT_STATUS_FLAG, false);
    lastJsonStatus = statusFlag;

    LegalJsonMessage jsonMessage = LegalJsonMessage.UNKNOWN;
    if (intent.hasExtra(Constant.INTENT_JSON_TYPE)) {
      String temp = intent.getStringExtra(Constant.INTENT_JSON_TYPE);
      jsonMessage = LegalJsonMessage.discoverMatchingEnum(temp);
      messageType = jsonMessage;
    }

    LOG.debug("jsonMessage dispatch:" + jsonMessage + ":" + statusFlag);

    switch (jsonMessage) {
      case AUTHORIZATION:
        if (statusFlag) {
          broadcastAuthenticationStatus(true);
          discoverSorties();
          writeLocations();
        } else {
          broadcastAuthenticationStatus(false);
          broadcastUploadComplete();
        }
        break;
      case CONFIGURATION:
        if (statusFlag) {
          networkFacade.testAuthorization(UploadService.class, this);
        }
        break;
      case GEOLOCATION:
        if (statusFlag) {
          locationUploadSuccess();
          writeLocations();
        }
        break;
      case OBSERVATION:
        if (statusFlag) {
          observationUploadSuccess();
          writeObservations();
        }
        break;
      case SORTIE:
        if (statusFlag) {
          sortieUploadSuccess();
          if (++sortieNdx < sortieModelList.size()) {
            writeLocations();
          } else {
            broadcastUploadComplete();
            stopSelf();
          }
        }
        break;
      default:
        LOG.debug("message type:unknown");
        networkFacade.readRemoteConfiguration(UploadService.class, this);
    }

    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private void discoverSorties() {
    sortieNdx = 0;
    sortieModelList = dataBaseFacade.selectAllSorties(false, this);
    LOG.debug("sortie population:" + sortieModelList.size());
  }

  private void locationUploadSuccess() {
    broadcastUploadStatus(LegalJsonMessage.GEOLOCATION, locationModelList.size());

    if (locationModelList != null) {
      for (LocationModel locationModel:locationModelList) {
        //mark upload success
        dataBaseFacade.setLocationUpload(locationModel.getId(), this);
      }
    }
  }

  private void observationUploadSuccess() {
    broadcastUploadStatus(LegalJsonMessage.OBSERVATION, observationModelList.size());

    if (observationModelList != null) {
      for (ObservationModel observationModel:observationModelList) {
        //mark upload success
        dataBaseFacade.setObservationUpload(observationModel.getId(), this);
      }
    }
  }

  private void sortieUploadSuccess() {
    broadcastUploadStatus(LegalJsonMessage.SORTIE, 1);

    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    dataBaseFacade.setSortieUpload(sortieModel.getId(), this);
  }

  private void writeLocations() {
    LOG.debug("xxx xxx writeLocation xxx xxx ");

    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    String sortieUuid = sortieModel.getSortieUuid();

    locationModelList = dataBaseFacade.selectAllLocations(false, sortieUuid, MAX_ROWS, this);
    if (locationModelList.isEmpty()) {
      //all locations have been transferred, now move to observations
      writeObservations();
    } else {
      networkFacade.writeLocations(sortieUuid, locationModelList, UploadService.class, this);
    }
  }

  private void writeObservations() {
    LOG.debug("xxx xxx writeObservation xxx xxx ");

    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    String sortieUuid = sortieModel.getSortieUuid();

    observationModelList = dataBaseFacade.selectAllObservations(false, sortieUuid, MAX_ROWS, this);
    if (observationModelList.isEmpty()) {
      //all observations have been transferred, now move to sorties
      writeSortie();
    } else {
      networkFacade.writeObservations(sortieUuid, observationModelList, UploadService.class, this);
    }
  }

  private void writeSortie() {
    LOG.debug("xxx xxx writeSortie xxx xxx ");

    SortieModel sortieModel = sortieModelList.get(sortieNdx);
    networkFacade.writeSortie(sortieModel, UploadService.class, this);
  }

  private void broadcastAuthenticationStatus(boolean flag) {
    Intent intent = new Intent(Constant.UPLOAD_EVENT);
    intent.putExtra(Constant.INTENT_AUTH_FLAG, flag);
    sendBroadcast(intent);
  }

  private void broadcastUploadStatus(final LegalJsonMessage messageType, int rowCount) {
    Intent intent = new Intent(Constant.UPLOAD_EVENT);
    intent.putExtra(Constant.INTENT_UPLOAD_TYPE, messageType.toString());
    intent.putExtra(Constant.INTENT_UPLOAD_COUNT, rowCount);
    sendBroadcast(intent);
  }

  private void broadcastUploadComplete() {
    Intent intent = new Intent(Constant.UPLOAD_EVENT);
    intent.putExtra(Constant.INTENT_COMPLETE_FLAG, true);
    sendBroadcast(intent);
  }
}