package com.digiburo.mellow.heeler.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;
import com.digiburo.mellow.heeler.lib.utility.UuidHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * user preferences
 */
public class SettingFragment extends Fragment {
  private static final Logger LOG = LoggerFactory.getLogger(SettingFragment.class);

  UserPreferenceHelper userPreferenceHelper;

  private CheckBox cbAudio;
  private CheckBox cbSpeech;
  private Spinner spinnerDistance;
  private Spinner spinnerTime;

  /**
   * mandatory empty ctor
   */
  public SettingFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    userPreferenceHelper = new UserPreferenceHelper(activity);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_setting, container, false);

    cbAudio = (CheckBox) view.findViewById(R.id.cbAudio);
    cbSpeech = (CheckBox) view.findViewById(R.id.cbSpeech);
    spinnerDistance = (Spinner) view.findViewById(R.id.spinnerDistance);
    spinnerTime = (Spinner) view.findViewById(R.id.spinnerTime);

    TextView tvInstallationId = (TextView) view.findViewById(R.id.tvInstallationId);
    tvInstallationId.setText(UuidHelper.formatUuidString(userPreferenceHelper.getInstallationId(getActivity())));

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    cbAudio.setChecked(userPreferenceHelper.isAudioCue(getActivity()));
    cbAudio.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        userPreferenceHelper.setAudioCue(getActivity(), cbAudio.isChecked());
      }
    });

    cbSpeech.setChecked(userPreferenceHelper.isSpeechCue(getActivity()));
    cbSpeech.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        userPreferenceHelper.setSpeechCue(getActivity(), cbSpeech.isChecked());
      }
    });

    ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.distance_option, android.R.layout.simple_spinner_item);
    distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinnerDistance.setAdapter(distanceAdapter);
    spinnerDistance.setSelection(discoverDistance());
    spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        LOG.info("onItemSelected:" + position + ":" + id);
        updateDistance(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
        LOG.info("onNothingSelected");
      }
    });

    ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.wifi_poll_option, android.R.layout.simple_spinner_item);
    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinnerTime.setAdapter(timeAdapter);
    spinnerTime.setSelection(discoverTime());

    spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        LOG.info("onItemSelected:" + position + ":" + id);
        updateTime(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {
        LOG.info("onNothingSelected");
      }
    });
  }

  private void updateDistance(int ndx) {
    String[] optionValues = getResources().getStringArray(R.array.distance_option_values);
    userPreferenceHelper.setPollDistance(getActivity(), Integer.parseInt(optionValues[ndx]));
  }

  private int discoverDistance() {
    String[] optionValues = getResources().getStringArray(R.array.distance_option_values);

    String target = Integer.toString(userPreferenceHelper.getPollDistance(getActivity()));
    for (int ii = 0; ii < optionValues.length; ii++) {
      if (optionValues[ii].equals(target)) {
        return ii;
      }
    }

    return 0;
  }

  private void updateTime(int ndx) {
    String[] optionValues = getResources().getStringArray(R.array.wifi_poll_option_values);
    userPreferenceHelper.setPollFrequency(getActivity(), Integer.parseInt(optionValues[ndx]));
  }

  private int discoverTime() {
    String[] optionValues = getResources().getStringArray(R.array.wifi_poll_option_values);

    String target = Integer.toString(userPreferenceHelper.getPollFrequency(getActivity()));
    for (int ii = 0; ii < optionValues.length; ii++) {
      if (optionValues[ii].equals(target)) {
        return ii;
      }
    }

    return 0;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */