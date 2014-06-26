package com.digiburo.mellow.heeler.app.ui;

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
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.digiburo.mellow.heeler.lib.utility.UuidHelper;

/**
 * @author gsc
 */
public class SortieDialog extends DialogFragment {
  public static final String FRAGMENT_TAG = "TAG_DIALOG";
  public static final String UUID = "UUID";

  private SortieModel sortieModel;

  private TextView textName;
  private TextView textUuid;
  private TextView textTime;

  private Button buttonDismiss;
  private Button buttonMap;

  private MainListener mainListener;

  public static SortieDialog newInstance(String uuid) {
    SortieDialog sortieDialog = new SortieDialog();

    Bundle bundle = new Bundle();
    bundle.putString(UUID, uuid);
    sortieDialog.setArguments(bundle);

    return sortieDialog;
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
    sortieModel = dataBaseFacade.selectSortie(uuid, getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_sortie, container, false);
    getDialog().setTitle(R.string.dialog_sortie);

    textName = (TextView) view.findViewById(R.id.textName);
    textName.setText(sortieModel.getSortieName());

    textUuid = (TextView) view.findViewById(R.id.textUuid);
    textUuid.setText(UuidHelper.formatUuidString(sortieModel.getSortieUuid()));

    textTime = (TextView) view.findViewById(R.id.textTime);
    textTime.setText(TimeUtility.secondsOnly(sortieModel.getTimeStamp()));

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
        mainListener.displayGoogleMap(sortieModel);
        dismiss();
      }
    });

    return view;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on June 25, 2014 by gsc
 */