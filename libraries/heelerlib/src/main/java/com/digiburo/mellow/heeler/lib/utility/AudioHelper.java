package com.digiburo.mellow.heeler.lib.utility;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
/**
 * @author gsc
 */
public class AudioHelper {

  private boolean preferenceTest(final Context context) {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(context);
    return userPreferenceHelper.isAudioCue(context);
  }

  public void notifier(final Context context) {
    if (preferenceTest(context)) {
      try {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();
      } catch (Exception exception) {
        //empty
      }
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 17, 2014 by gsc
 */
