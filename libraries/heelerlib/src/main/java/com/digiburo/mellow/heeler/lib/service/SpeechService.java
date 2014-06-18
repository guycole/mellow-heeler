package com.digiburo.mellow.heeler.lib.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;

/**
 * Speech
 */
public class SpeechService extends IntentService implements TextToSpeech.OnInitListener {
  private static final Logger LOG = LoggerFactory.getLogger(SpeechService.class);

  private final ProgressListener progressListener = new ProgressListener(this);

  private boolean sleepFlag = true;
  private TextToSpeech textToSpeech;
  private String phrase;

  public static void sayThis(String arg, Context context) {
    Intent intent = new Intent(context, SpeechService.class);
    intent.setAction(Constant.INTENT_ACTION_SAY_THIS);
    intent.putExtra(Constant.INTENT_SAY_THIS, arg);
    context.startService(intent);
  }

  /**
   * mandatory empty ctor
   */
  public SpeechService() {
    super("SpeechService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    LOG.debug("xxx xxx onCreate xxx xxx");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    LOG.debug("xxx xxx onDestroy xxx xxx");
//    textToSpeech.shutdown();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    LOG.debug("xxx xxx onHandleIntent xxx xxx");

    if (intent != null) {
      LOG.debug("action:" + intent.getAction());

      if (Constant.INTENT_ACTION_SAY_THIS.equals(intent.getAction())) {
        phrase = intent.getStringExtra(Constant.INTENT_SAY_THIS);
        LOG.debug("phrase:" + phrase);

        if (preferenceTest()) {
          textToSpeech = new TextToSpeech(this, this);
          textToSpeech.setOnUtteranceProgressListener(progressListener);

          sleepForCompletion();
        }
      }
    }
  }

  //TextToSpeech.OnInitListener
  public void onInit(int status) {
    LOG.info("onInit:" + status);

    if (status == TextToSpeech.SUCCESS) {
      LOG.info("onInit success");

      int result = textToSpeech.setLanguage(Locale.US);
      if ((result == TextToSpeech.LANG_MISSING_DATA) || (result == TextToSpeech.LANG_NOT_SUPPORTED)) {
        throw new IllegalArgumentException("unsupported language");
      } else {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "2718");

        textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, map);
      }
    } else {
      LOG.info("onInit failure");
    }
  }

  public void wakeUp() {
    sleepFlag = false;
  }

  private void sleepForCompletion() {
    int maxCount = 12;
    int testCount = 0;

    sleepFlag = true;

    do {
      try {
        ++testCount;
        Thread.sleep(1 * 1000L);
//        System.out.println("sleeping:" + testCount);
      } catch (Exception exception) {
        testCount = maxCount + 1;
      }
    } while ((testCount < maxCount) && (sleepFlag));

    textToSpeech.shutdown();
  }

  private boolean preferenceTest() {
    UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper(this);
    return userPreferenceHelper.isSpeechCue(this);
  }
}
