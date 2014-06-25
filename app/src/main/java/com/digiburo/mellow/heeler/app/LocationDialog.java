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
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.UuidHelper;

/**
 * @author gsc
 */
public class LocationDialog extends DialogFragment {
  public static final String FRAGMENT_TAG = "TAG_DIALOG";
  public static final String UUID = "UUID";

  private LocationModel locationModel;

  private TextView textAccuracy;
  private TextView textAltitude;
  private TextView textLatitude;
  private TextView textLongitude;
  private TextView textUuid;
  private TextView textTime;

  private Button buttonDismiss;
  private Button buttonMap;
  private Button buttonSortieDetail;

  private MainListener mainListener;

  public static LocationDialog newInstance(String uuid) {
    LocationDialog locationDialog = new LocationDialog();

    Bundle bundle = new Bundle();
    bundle.putString(UUID, uuid);
    locationDialog.setArguments(bundle);

    return locationDialog;
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
    locationModel = dataBaseFacade.selectLocation(uuid, getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_location, container, false);
    getDialog().setTitle(R.string.dialog_location);

    textAccuracy = (TextView) view.findViewById(R.id.textAccuracy);
    textAltitude = (TextView) view.findViewById(R.id.textAltitude);
    textLatitude = (TextView) view.findViewById(R.id.textLatitude);
    textLongitude = (TextView) view.findViewById(R.id.textLongitude);
    textUuid = (TextView) view.findViewById(R.id.textUuid);
    textTime = (TextView) view.findViewById(R.id.textTime);

    buttonDismiss = (Button) view.findViewById(R.id.buttonDismiss);
    buttonDismiss.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });

    buttonMap = (Button) view.findViewById(R.id.buttonMap);
    buttonMap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mainListener.displayGoogleMap(locationModel);
        dismiss();
      }
    });

    buttonSortieDetail = (Button) view.findViewById(R.id.buttonSortieDetail);
    buttonSortieDetail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mainListener.displaySortieDetail(locationModel.getSortieUuid());
      }
    });

    String accuracy = String.format("%.4f", (double) locationModel.getAccuracy());
    String altitude = String.format("%.4f", (double) locationModel.getAltitude());
    String lat = String.format("%.4f", (double) locationModel.getLatitude());
    String lng = String.format("%.4f", (double) locationModel.getLongitude());

    textAccuracy.setText(accuracy);
    textAltitude.setText(altitude);
    textLatitude.setText(lat);
    textLongitude.setText(lng);
    textUuid.setText(UuidHelper.formatUuidString(locationModel.getLocationUuid()));
    textTime.setText(TimeUtility.secondsOnly(locationModel.getTimeStamp()));

    return (view);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 25, 2014 by gsc
 */