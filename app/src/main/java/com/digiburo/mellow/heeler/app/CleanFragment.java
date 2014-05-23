package com.digiburo.mellow.heeler.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.CleanController;
import com.digiburo.mellow.heeler.lib.UploadController;

/**
 * upload fragment
 */
public class CleanFragment extends Fragment {
  public static final String LOG_TAG = CleanFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public CleanFragment() {
    //empty
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_clean, container, false);
    return (view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    CleanController cleanController = new CleanController();
    cleanController.cleanDataBase(getActivity());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */