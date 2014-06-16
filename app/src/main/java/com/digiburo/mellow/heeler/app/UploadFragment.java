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
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.digiburo.mellow.heeler.lib.service.UploadService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * upload fragment
 */
public class UploadFragment extends Fragment {
  private static final Logger LOG = LoggerFactory.getLogger(UploadFragment.class);

  private SortieModelList sortieModelList;

  /**
   * update display for fresh location or WiFi detection event
   */
  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LOG.info("onReceive noted");

      boolean authFlag = false;
      boolean uploadFlag = false;

      if (intent.hasExtra(Constant.INTENT_AUTH_FLAG)) {
        authFlag = intent.getBooleanExtra(Constant.INTENT_AUTH_FLAG, false);
        if (authFlag) {
          Toast.makeText(getActivity(), "authentication success", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(getActivity(), "authentication failure", Toast.LENGTH_LONG).show();
        }
      } else if (intent.hasExtra(Constant.INTENT_UPLOAD_FLAG)) {
        uploadFlag = intent.getBooleanExtra(Constant.INTENT_UPLOAD_FLAG, false);
        if (uploadFlag) {
          Toast.makeText(getActivity(), "upload success", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(getActivity(), "upload failure", Toast.LENGTH_LONG).show();
        }

        getActivity().finish();
      }
    }
  };

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
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constant.UPLOAD_EVENT));

    /*
    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    sortieModelList = dataBaseFacade.selectAllSorties(false, getActivity());
    if (sortieModelList.isEmpty()) {
      LOG.debug("empty sortie list");
      Toast.makeText(getActivity(), "empty upload list", Toast.LENGTH_LONG).show();
      getActivity().finish();
      return;
    }
    */

    getActivity().startService(new Intent(getActivity(), UploadService.class));
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