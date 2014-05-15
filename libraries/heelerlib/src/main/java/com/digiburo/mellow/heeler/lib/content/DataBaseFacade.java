package com.digiburo.mellow.heeler.lib.content;

import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class DataBaseFacade {
  private static final Logger LOG = LoggerFactory.getLogger(DataBaseFacade.class);

  public boolean newLocation(LocationModel model, Context context) {
    Uri result = context.getContentResolver().insert(LocationTable.CONTENT_URI, model.toContentValues(context));
    LOG.debug("new location:" + result);
    return true;
  }

  public boolean newObservation(ObservationModel model, Context context) {
    Uri result = context.getContentResolver().insert(ObservationTable.CONTENT_URI, model.toContentValues(context));
    LOG.debug("new observation:" + result);
    return true;
  }
}
