package com.digiburo.mellow.heeler.lib.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AudioService extends IntentService implements TextToSpeech.OnInitListener {
  private static final Logger LOG = LoggerFactory.getLogger(AudioService.class);

  private static final String ACTION_RECEIVE_POPULATION = "com.digiburo.mellow.heeler.lib.service.action.RECEIVE_POPULATION";
  private static final String ACTION_LOCATION_UPDATE_CUE = "com.digiburo.mellow.heeler.lib.service.action.LOCATION_UPDATE_CUE";

  private static final String RECEIVE_POPULATION = "com.digiburo.mellow.heeler.lib.service.extra.RECEIVE_POPULATION";

  private TextToSpeech textToSpeech;
  private String phrase;

  public static void startActionSayReceivePopulation(Context context, int population) {
    Intent intent = new Intent(context, AudioService.class);
    intent.setAction(ACTION_RECEIVE_POPULATION);
    intent.putExtra(RECEIVE_POPULATION, population);
    context.startService(intent);
  }

  public AudioService() {
        super("AudioService");
    }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_RECEIVE_POPULATION.equals(action)) {
        final int receivePopulation = intent.getIntExtra(RECEIVE_POPULATION, 0);
        handleActionReceivePopulation(receivePopulation);

        try {
          Thread.sleep(5000L);
        } catch(Exception exception) {
          //
        }
      } else if (ACTION_LOCATION_UPDATE_CUE.equals(action)) {
        handleLocationUpdateCue();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("xxx xxx onDestroy xxx xxx ");
  }

  private void handleActionReceivePopulation(int population) {
    LOG.info("speak:" + population);
    phrase = Integer.toString(population);
    textToSpeech = new TextToSpeech(this, this);
  }

  //TextToSpeech.OnInitListener
  public void onInit(int status) {
    LOG.info("speak:" + status + ":" + phrase);
    if (status == TextToSpeech.SUCCESS) {
      int result = textToSpeech.setLanguage(Locale.US);
      if ((result == TextToSpeech.LANG_MISSING_DATA) || (result == TextToSpeech.LANG_NOT_SUPPORTED)) {
        throw new IllegalArgumentException("unsupported language");
      } else {
        textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null);
      }
    }
  }

  private void handleLocationUpdateCue() {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
