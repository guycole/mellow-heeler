package com.digiburo.mellow.heeler.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digiburo.mellow.heeler.R;

/**
 * user preferences
 */
public class PrefFragment extends PreferenceFragment {
  public static final String LOG_TAG = PrefFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public PrefFragment() {
    //empty
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.prefer);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    return(view);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */