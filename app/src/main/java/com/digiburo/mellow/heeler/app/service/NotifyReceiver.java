package com.digiburo.mellow.heeler.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.app.ui.MainActivity;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.utility.LegalMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * update notification manager of collection event
 * @author gsc
 */
public class NotifyReceiver extends BroadcastReceiver {
  private static final Logger LOG = LoggerFactory.getLogger(NotifyReceiver.class);

  public void onReceive(Context context, Intent intent) {
    LOG.debug("xxx xxx onReceive xxx xxx:" + intent.getAction());

    int observed = intent.getIntExtra(Constant.INTENT_OBSERVATION_UPDATE, 0);
//    boolean modeFlag = intent.getBooleanExtra(Constant.INTENT_MODE_UPDATE, false);
    boolean updateFlag = intent.getBooleanExtra(Constant.INTENT_LOCATION_UPDATE, false);

    Long observationPopulation = 0L;
    if (Personality.getCurrentSortie() != null) {
      DataBaseFacade dataBaseFacade = new DataBaseFacade(context);
      observationPopulation = dataBaseFacade.countObservationRows(true, Personality.getCurrentSortie().getSortieUuid(), context);
    }

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(new Intent(context, MainActivity.class));

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentIntent(pendingIntent);
    builder.setContentTitle("Mellow Heeler");
    builder.setSmallIcon(R.drawable.icon24);

    if (Personality.getOperationMode() == LegalMode.COLLECTION) {
      builder.setContentText(context.getText(R.string.text_collection));
      builder.setNumber(observationPopulation.intValue());
    } else {
      builder.setContentText(context.getText(R.string.text_idle));
      builder.setNumber(0);
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(Constant.NOTIFICATION_ID, builder.build());
  }
}
