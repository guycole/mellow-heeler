package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.UuidHelper;

/**
 * @author gsc
 */
public class ObservationDialog extends DialogFragment {
  public static final String FRAGMENT_TAG = "TAG_DIALOG";
  public static final String UUID = "UUID";

  private ObservationModel observationModel;

  private TextView textBssid;
  private TextView textCapability;
  private TextView textFrequency;
  private TextView textLevel;
  private TextView textObservation;
  private TextView textSsid;
  private TextView textTime;

  private Button buttonDismiss;
  private Button buttonLocationDetail;
  private Button buttonMap;
  private Button buttonSortieDetail;

  private MainListener mainListener;

  public static ObservationDialog newInstance(String uuid) {
    ObservationDialog observationDialog = new ObservationDialog();

    Bundle bundle = new Bundle();
    bundle.putString(UUID, uuid);
    observationDialog.setArguments(bundle);

    return observationDialog;
  }

  /**
   * invoke me
   */
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mainListener = (MainListener) activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String uuid = getArguments().getString(UUID);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    observationModel = dataBaseFacade.selectObservation(uuid, getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_observation, container, false);
    getDialog().setTitle(R.string.dialog_observation);

    textBssid = (TextView) view.findViewById(R.id.textBssid);
    textCapability = (TextView) view.findViewById(R.id.textCapability);
    textFrequency = (TextView) view.findViewById(R.id.textFrequency);
    textLevel = (TextView) view.findViewById(R.id.textLevel);
    textObservation = (TextView) view.findViewById(R.id.textObservation);
    textSsid = (TextView) view.findViewById(R.id.textSsid);
    textTime = (TextView) view.findViewById(R.id.textTime);

    buttonDismiss = (Button) view.findViewById(R.id.buttonDismiss);
    buttonDismiss.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    buttonLocationDetail = (Button) view.findViewById(R.id.buttonLocationDetail);
    buttonLocationDetail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mainListener.displayLocationDetail(observationModel.getLocationUuid());
      }
    });

    buttonMap = (Button) view.findViewById(R.id.buttonMap);
    buttonMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mainListener.displayGoogleMap(observationModel);
        dismiss();
      }
    });

    buttonSortieDetail = (Button) view.findViewById(R.id.buttonSortieDetail);
    buttonSortieDetail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mainListener.displaySortieDetail(observationModel.getSortieUuid());
      }
    });

    textBssid.setText(observationModel.getBssid());
    textCapability.setText(observationModel.getCapability());
    textFrequency.setText(Integer.toString(observationModel.getFrequency()));
    textLevel.setText(Integer.toString(observationModel.getLevel()));
    textObservation.setText(UuidHelper.formatUuidString(observationModel.getObservationUuid()));
    textSsid.setText(observationModel.getSsid());
    textTime.setText(TimeUtility.secondsOnly(observationModel.getTimeStamp()));

    return view;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 25, 2014 by gsc
 */