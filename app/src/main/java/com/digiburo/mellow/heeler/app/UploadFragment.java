package com.digiburo.mellow.heeler.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.UploadController;

/**
 * upload fragment
 */
public class UploadFragment extends Fragment {
  public static final String LOG_TAG = UploadFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public UploadFragment() {
    //empty
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_upload, container, false);
    return (view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    UploadController uploadController = new UploadController();
    uploadController.uploadAll(getActivity());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */