package com.digiburo.mellow.heeler.lib.service;

import android.speech.tts.UtteranceProgressListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */
public class ProgressListener extends UtteranceProgressListener {
  private static final Logger LOG = LoggerFactory.getLogger(ProgressListener.class);

  private SpeechService parent;

  public ProgressListener(SpeechService parent) {
    this.parent = parent;
  }

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onDone(String utteranceId) {
    LOG.info("utteranceProgressListener.onDone:" + utteranceId);
    parent.wakeUp();
  }

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onError(String utteranceId) {
    LOG.info("utteranceProgressListener.onError:" + utteranceId);
    parent.wakeUp();
  }

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onStart(String utteranceId) {
    LOG.info("utteranceProgressListener.onStart:" + utteranceId);
  }
}
