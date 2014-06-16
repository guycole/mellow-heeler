package com.digiburo.mellow.heeler.lib.service;

import android.speech.tts.UtteranceProgressListener;

import com.sun.javaws.progress.Progress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gsc
 */


/* 
 * Copyright 2014 Digital Burro, INC
 * Created 6/16/14 by gsc
 */

public class ProgressListener extends UtteranceProgressListener {

  private static final Logger LOG = LoggerFactory.getLogger(ProgressListener.class);

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onDone(String utteranceId) {
    LOG.info("utteranceProgressListener.onDone:" + utteranceId);
  }

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onError(String utteranceId) {
    LOG.info("utteranceProgressListener.onError:" + utteranceId);
  }

  /**
   * UtteranceProgressListener
   * @param utteranceId
   */
  public void onStart(String utteranceId) {
    LOG.info("utteranceProgressListener.onStart:" + utteranceId);
  }
}
