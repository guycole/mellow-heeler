package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.google.android.gms.plus.model.people.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * upload fragment
 */
public class ConsoleFragment extends Fragment {
  public static final String FRAGMENT_TAG = "TAG_CONSOLE";

  public static final int MAX_ROWS = 4000;

  private static final Logger LOG = LoggerFactory.getLogger(ConsoleFragment.class);

  private Button buttonSpecialLocation;

  private EditText editSortieName;

  private TextView textLastLocation;
  private TextView textLastLocationTime;
  private TextView textLocationRowCount;
  private TextView textLastSsid;
  private TextView textLastSsidTime;
  private TextView textObservationRowCount;
  private TextView textStatus;

  private ToggleButton toggleStart;

  //TODO runFlag persist to bundle
  private boolean runFlag = false;

  private MainListener mainListener;

  /**
   * update display for fresh location or WiFi detection event
   */
  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LOG.info("onReceive noted");

      if (intent.hasExtra(Constant.INTENT_MODE_FLAG)) {
        runFlag = intent.getBooleanExtra(Constant.INTENT_MODE_FLAG, false);
        updateStatusLabel();

        if (runFlag) {
          updateSortieName();
        }
      } else {
        long rowKey = intent.getLongExtra(Constant.INTENT_ROW_KEY, 0);
        if (rowKey < 1) {
          updateLocationDisplay();

          WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getActivity(), R.string.toast_wifi_disabled, Toast.LENGTH_LONG).show();
          }
        } else {
          updateDetectionDisplay(rowKey);
        }
      }
    }
  };

  /**
   * mandatory empty ctor
   */
  public ConsoleFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainListener = (MainListener) activity;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_console, container, false);
    return(view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    buttonSpecialLocation = (Button) getActivity().findViewById(R.id.buttonSpecialLocation);
    buttonSpecialLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        serviceSpecialLocation();
      }
    });

    editSortieName = (EditText) getActivity().findViewById(R.id.editSortieName);
    editSortieName.setText(Constant.DEFAULT_SORTIE_NAME);

    textLastLocation = (TextView) getActivity().findViewById(R.id.textLastLocation);
    textLastLocation.setText(Constant.UNKNOWN);

    textLastLocationTime = (TextView) getActivity().findViewById(R.id.textLastLocationTime);
    textLastLocationTime.setText(Constant.UNKNOWN);

    textLocationRowCount = (TextView) getActivity().findViewById(R.id.textLocationRowCount);
    textLocationRowCount.setText(Constant.UNKNOWN);

    textLastSsid = (TextView) getActivity().findViewById(R.id.textLastSsid);
    textLastSsid.setText(Constant.UNKNOWN);

    textLastSsidTime = (TextView) getActivity().findViewById(R.id.textLastSsidTime);
    textLastSsidTime.setText(Constant.UNKNOWN);

    textObservationRowCount = (TextView) getActivity().findViewById(R.id.textObservationRowCount);
    textObservationRowCount.setText(Constant.UNKNOWN);

    textStatus = (TextView) getActivity().findViewById(R.id.textStatus);

    toggleStart = (ToggleButton) getActivity().findViewById(R.id.toggleStart);

    updateStatusLabel();
  }

  @Override
  public void onResume() {
    super.onResume();
    updateStatusLabel();
    updateDetectionDisplay(0);
    updateLocationDisplay();
    getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constant.FRESH_UPDATE));
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(broadcastReceiver);
  }

  /**
   * manage status label
   */
  private void updateStatusLabel() {
    int colorBackground = Color.RED;
    int colorText = Color.GREEN;
    int message = R.string.text_idle;

    if (runFlag) {
      message = R.string.text_collection;
      colorBackground = Color.GREEN;
      colorText = Color.RED;

      toggleStart.setChecked(true);
      LOG.debug("start collection true");
    } else {
      toggleStart.setChecked(false);
      editSortieName.setText("");
      LOG.debug("start collection false");
    }

    textStatus.setText(message);
    textStatus.setBackgroundColor(colorBackground);
    textStatus.setTextColor(colorText);
  }

  /**
   * ensure that user supplied sortie name is persisted
   */
  private void updateSortieName() {
    String tempName = editSortieName.getText().toString();
    SortieModel sortieModel = Personality.getCurrentSortie();
    if (sortieModel != null) {
      if (tempName.isEmpty()) {
        editSortieName.setText(sortieModel.getSortieName());
        return;
      }

      if (Constant.DEFAULT_SORTIE_NAME.equals(tempName)) {
        editSortieName.setText(sortieModel.getSortieName());
      } else if (Constant.DEFAULT_SORTIE_NAME.equals(sortieModel.getSortieName())) {
        sortieModel.setSortieName(tempName);

        DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
        dataBaseFacade.updateSortie(sortieModel, getActivity());

        //editSortieName.setText(sortieModel.getSortieName());
      }
    }
  }

  private void updateDetectionDisplay(long rowKey) {
    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());

    String ssid = "Unknown";
    String timeStamp = "Unknown";

    if (rowKey > 0) {
      ObservationModel observationModel = dataBaseFacade.selectObservation(rowKey, getActivity());
      ssid = observationModel.getSsid();
      timeStamp = observationModel.getTimeStamp();
    }

    textLastSsid.setText(ssid);
    textLastSsidTime.setText(timeStamp);

    if (Personality.getCurrentSortie() == null) {
      textObservationRowCount.setText("Empty");
    } else {
      int observationPopulation = dataBaseFacade.countObservationRows(Personality.getCurrentSortie().getSortieUuid(), getActivity());
      textObservationRowCount.setText(Integer.toString(observationPopulation));

      if (observationPopulation > MAX_ROWS) {
        mainListener.restartSortie(editSortieName.getText().toString());
      }
    }
  }

  private void updateLocationDisplay() {
    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel != null) {
      String latString = String.format("%.6f", (double) locationModel.getLatitude());
      String lngString = String.format("%.6f", (double) locationModel.getLongitude());
      textLastLocation.setText(latString + ", " + lngString);
      textLastLocationTime.setText(locationModel.getTimeStamp());

      if (Personality.getCurrentSortie() == null) {
        textLocationRowCount.setText("Empty");
      } else {
        DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
        int locationPopulation = dataBaseFacade.countLocationRows(Personality.getCurrentSortie().getSortieUuid(), getActivity());
        textLocationRowCount.setText(Integer.toString(locationPopulation));

        if (locationPopulation > MAX_ROWS) {
          mainListener.restartSortie(editSortieName.getText().toString());
        }
      }
    }
  }

  /**
   * special location button
   */
  private void serviceSpecialLocation() {
    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel == null) {
      LOG.debug("unable to update null location");
      Toast.makeText(getActivity(), R.string.toast_location_failure, Toast.LENGTH_LONG).show();
    } else {
      LOG.debug("updating current location");
      locationModel.setSpecialFlag();
      DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
      dataBaseFacade.updateLocation(locationModel, getActivity());
      Toast.makeText(getActivity(), R.string.toast_location_success, Toast.LENGTH_LONG).show();
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 24, 2014 by gsc
 */