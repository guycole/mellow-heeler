package com.digiburo.mellow.heeler.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.utility.LegalJsonMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exists solely for testing and is not part of production code
 */
public class NetworkFacadeService extends Service {
  public static final int MAX_ROWS = 30;
  public static final int MAX_COUNT = 12;

  //testing kludges
  public boolean created = false;
  public int startId = 0;
  public boolean lastJsonStatus = false;
  public LegalJsonMessage messageType = LegalJsonMessage.UNKNOWN;

  private static final Logger LOG = LoggerFactory.getLogger(NetworkFacadeService.class);

  /**
   * mandatory empty ctor
   */
  public NetworkFacadeService() {
    //empty
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }


  @Override
  public void onCreate() {
    super.onCreate();
    created = true;
    LOG.debug("xxx xxx onCreate xxx xxx");
    //dataBaseFacade = new DataBaseFacade(this);
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

    return START_STICKY;
  }
}
