package com.digiburo.mellow.heeler.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.CleanController;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * clean fragment
 */
public class CleanFragment extends Fragment {
  private static final Logger LOG = LoggerFactory.getLogger(CleanFragment.class);

  private final CleanController cleanController = new CleanController();

  private Button buttonDeleteAll;
  private Button buttonDeleteUploaded;

  private TextView textLocationTotal;
  private TextView textLocationUploaded;

  private TextView textObservationTotal;
  private TextView textObservationUploaded;

  private TextView textSortieTotal;
  private TextView textSortieUploaded;

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

    textLocationTotal = (TextView) view.findViewById(R.id.textLocationTotal);
    textLocationTotal.setText(Constant.UNKNOWN);

    textLocationUploaded = (TextView) view.findViewById(R.id.textLocationUploaded);
    textLocationUploaded.setText(Constant.UNKNOWN);

    textObservationTotal = (TextView) view.findViewById(R.id.textObservationTotal);
    textObservationTotal.setText(Constant.UNKNOWN);

    textObservationUploaded = (TextView) view.findViewById(R.id.textObservationUploaded);
    textObservationUploaded.setText(Constant.UNKNOWN);

    textSortieTotal = (TextView) view.findViewById(R.id.textSortieTotal);
    textSortieTotal.setText(Constant.UNKNOWN);

    textSortieUploaded = (TextView) view.findViewById(R.id.textSortieUploaded);
    textSortieUploaded.setText(Constant.UNKNOWN);

    buttonDeleteAll = (Button) view.findViewById(R.id.buttonDeleteAll);
    buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        cleanController.deleteAll(getActivity());
        updatePopulations();
      }
    });

    buttonDeleteUploaded = (Button) view.findViewById(R.id.buttonDeleteUploaded);
    buttonDeleteUploaded.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        cleanController.deleteUploaded(getActivity());
        updatePopulations();
      }
    });

    updatePopulations();

    return view;
  }

  private void updatePopulations() {
    long locationTotal = 0;
    long locationUploaded = 0;
    long observationTotal = 0;
    long observationUploaded = 0;
    long sortieTotal = 0;
    long sortieUploaded = 0;
    long temp1, temp2;

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());

    SortieModelList sortieModelList = dataBaseFacade.selectAllSorties(true, getActivity());
    sortieTotal = sortieModelList.size();

    for (SortieModel sortieModel:sortieModelList) {
      if (sortieModel.isUploadFlag()) {
        ++sortieUploaded;
      }

      temp1 = dataBaseFacade.countLocationRows(true, sortieModel.getSortieUuid(), getActivity());
      temp2 = dataBaseFacade.countLocationRows(false, sortieModel.getSortieUuid(), getActivity());

      locationTotal += temp1;
      locationUploaded += (temp1 - temp2);

      temp1 = dataBaseFacade.countObservationRows(true, sortieModel.getSortieUuid(), getActivity());
      temp2 = dataBaseFacade.countObservationRows(false, sortieModel.getSortieUuid(), getActivity());

      observationTotal += temp1;
      observationUploaded += (temp1 - temp2);
    }

    textLocationTotal.setText(Long.toString(locationTotal));
    textLocationUploaded.setText(Long.toString(locationUploaded));

    textObservationTotal.setText(Long.toString(observationTotal));
    textObservationUploaded.setText(Long.toString(observationUploaded));

    textSortieTotal.setText(Long.toString(sortieTotal));
    textSortieUploaded.setText(Long.toString(sortieUploaded));
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 21, 2014 by gsc
 */