package com.digiburo.mellow.heeler.app;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.CleanController;
import com.digiburo.mellow.heeler.lib.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * upload fragment
 */
public class CleanFragment extends Fragment {
  private static final Logger LOG = LoggerFactory.getLogger(UploadFragment.class);

  /**
   * update display for fresh location or WiFi detection event
   */
  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
    LOG.info("onReceive noted");
    getActivity().finish();
    }
  };

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
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constant.CLEAN_EVENT));

    CleanController cleanController = new CleanController();
    cleanController.cleanDataBase(getActivity());

    Toast.makeText(getActivity(), "clean complete", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(broadcastReceiver);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */