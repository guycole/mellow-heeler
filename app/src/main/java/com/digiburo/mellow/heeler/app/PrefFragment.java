package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digiburo.mellow.heeler.R;

/**
 * user preferences
 */
public class PrefFragment extends Fragment {
  public static final String LOG_TAG = PrefFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public PrefFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_pref, container, false);
    return(view);
  }

}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */