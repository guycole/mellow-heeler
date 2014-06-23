package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;

/**
 * @author gsc
 */
public class ObservationDialog extends DialogFragment {
  public static final String FRAGMENT_TAG = "TAG_OBSERVATION_DIALOG";
  public static final String ROW_KEY = "ROW_KEY";

  private ObservationModel observationModel;

  private TextView textBssid;
  private TextView textCapability;
  private TextView textFrequency;
  private TextView textLevel;
  private TextView textLocation;
  private TextView textObservation;
  private TextView textSortie;
  private TextView textSsid;
  private TextView textTime;

  private Button button;

  private MainListener mainListener;

  private Handler handler = new Handler();

  public static ObservationDialog newInstance(long rowId) {
    ObservationDialog observationDialog = new ObservationDialog();

    Bundle bundle = new Bundle();
    bundle.putLong(ROW_KEY, rowId);
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
    long rowId = getArguments().getLong(ROW_KEY);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    observationModel = dataBaseFacade.selectObservation(rowId, getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_observation, container, false);
    getDialog().setTitle(R.string.dialog_observation);

    textBssid = (TextView) view.findViewById(R.id.textBssid);
    textCapability = (TextView) view.findViewById(R.id.textCapability);
    textFrequency = (TextView) view.findViewById(R.id.textFrequency);
    textLevel = (TextView) view.findViewById(R.id.textLevel);
    textLocation = (TextView) view.findViewById(R.id.textLocation);
    textObservation = (TextView) view.findViewById(R.id.textObservation);
    textSortie = (TextView) view.findViewById(R.id.textSortie);
    textSsid = (TextView) view.findViewById(R.id.textSsid);
    textTime = (TextView) view.findViewById(R.id.textTime);

    button = (Button) view.findViewById(R.id.buttonDismiss);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    return (view);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    textBssid.setText(observationModel.getBssid());
    textCapability.setText(observationModel.getCapability());
    textFrequency.setText(Integer.toString(observationModel.getFrequency()));
    textLevel.setText(Integer.toString(observationModel.getLevel()));
    textLocation.setText(observationModel.getLocationUuid());
    textObservation.setText(observationModel.getObservationUuid());
    textSortie.setText(observationModel.getSortieUuid());
    textSsid.setText(observationModel.getSsid());
    textTime.setText(observationModel.getTimeStamp());
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 22, 2014 by gsc
 */