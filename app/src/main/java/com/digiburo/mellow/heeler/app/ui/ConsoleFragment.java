package com.digiburo.mellow.heeler.app.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.SortieController;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.DataBaseTableIf;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieTable;
import com.digiburo.mellow.heeler.lib.utility.LegalMode;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;
import com.google.android.gms.plus.model.people.Person;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * console fragment
 */
public class ConsoleFragment extends ListFragment {
  public static final String FRAGMENT_TAG = "TAG_CONSOLE";

  //context menu
  public static final int CONTEXT_ITEM_1 = Menu.FIRST;

  private static final Logger LOG = LoggerFactory.getLogger(ConsoleFragment.class);

  private Button buttonSpecialLocation;
  private Button buttonStartStop;

  private EditText editSortieName;

  private TextView textGeoLoc;
  private TextView textLocationTime;
  private TextView textLocationRowCount;

  private TextView textObservationRowCount;

  private TextView textStatus;

  private MainListener mainListener;

  private ArrayAdapter<String> arrayAdapter;

  /**
   * update display for fresh location or WiFi detection event
   */
  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LOG.info("onReceive noted");

      updateStatusLabel();
      updateLocationDisplay();
      updateDetectionDisplay(intent.getIntExtra(Constant.INTENT_OBSERVATION_UPDATE, 0));

      WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (!wifiManager.isWifiEnabled()) {
        //TODO should be dialog not toast
        Toast.makeText(getActivity(), R.string.toast_wifi_disabled, Toast.LENGTH_LONG).show();
        mainListener.sortieStop();
      }

      if (!Personality.isGpsProvider()) {
        //TODO should be dialog not toast
        Toast.makeText(getActivity(), R.string.toast_gps_disabled, Toast.LENGTH_LONG).show();
        mainListener.sortieStop();
      }
    }
  };

  /**
   * short press
   */
  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    LOG.debug("onListItemClick:" + position + ":" + id);

    ObservationModel observationModel = Personality.getCurrentObserved().get(position);
    mainListener.displayObservationDetail(observationModel.getObservationUuid());
  }

  /**
   * delete row (long click)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
    menu.add(0, CONTEXT_ITEM_1, 0, R.string.menu_context_hot_list);
  }

  /**
   * service context menu
   * @param item
   * @return
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    LOG.debug("on context item select:" + item + ":" + info.id + ":" + arrayAdapter.getItem(info.position));

    switch(item.getItemId()) {
      case CONTEXT_ITEM_1:
        Long temp = new Long(info.id);
        ObservationModel observationModel = Personality.getCurrentObserved().get(temp.intValue());
        mainListener.addHot(observationModel);
        break;
    }

    return super.onContextItemSelected(item);
  }

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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_console, container, false);

    buttonSpecialLocation = (Button) view.findViewById(R.id.buttonSpecialLocation);
    buttonSpecialLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        serviceSpecialLocation();
      }
    });

    buttonStartStop = (Button) view.findViewById(R.id.buttonStartStop);
    buttonStartStop.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (Personality.getOperationMode() == LegalMode.COLLECTION) {
          mainListener.sortieStop();
        } else {
          String temp = editSortieName.getText().toString();
          if ((temp == null) || temp.trim().isEmpty()) {
            mainListener.sortieStart(Constant.DEFAULT_SORTIE_NAME);
          } else {
            mainListener.sortieStart(temp.trim());
          }
        }
      }
    });

    editSortieName = (EditText) view.findViewById(R.id.editSortieName);
    editSortieName.setText(Constant.DEFAULT_SORTIE_NAME);

    textGeoLoc = (TextView) view.findViewById(R.id.textGeoLoc);
    textGeoLoc.setText(Constant.UNKNOWN);

    textLocationTime = (TextView) view.findViewById(R.id.textLocationTime);
    textLocationTime.setText(Constant.UNKNOWN);

    textLocationRowCount = (TextView) view.findViewById(R.id.textLocationRowCount);
    textLocationRowCount.setText("Location Rows:" + Constant.EMPTY);

    textObservationRowCount = (TextView) view.findViewById(R.id.textObservationRowCount);
    textObservationRowCount.setText("Observation Rows:" + Constant.EMPTY);

    textStatus = (TextView) view.findViewById(R.id.textStatus);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    registerForContextMenu(getListView());

    updateStatusLabel();
  }

  @Override
  public void onResume() {
    super.onResume();
    updateStatusLabel();
    updateDetectionDisplay(0);
    updateLocationDisplay();
    getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constant.CONSOLE_UPDATE));
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(broadcastReceiver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mainListener = null;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    setListAdapter(null);
  }

  /**
   * manage status label
   */
  private void updateStatusLabel() {
    int colorBackground = Color.RED;
    int colorText = Color.GREEN;
    int message = R.string.text_idle;

    if (Personality.getOperationMode() == LegalMode.COLLECTION) {
      LOG.debug("start collection true");

      message = R.string.text_collection;
      colorBackground = Color.GREEN;
      colorText = Color.RED;

      buttonStartStop.setText(R.string.button_stop);

      editSortieName.setEnabled(false);
    } else {
      LOG.debug("start collection false");

      buttonStartStop.setText(R.string.button_start);

      editSortieName.setEnabled(true);
      editSortieName.setText("");
    }

    textStatus.setText(message);
    textStatus.setBackgroundColor(colorBackground);
    textStatus.setTextColor(colorText);
  }

  private void updateDetectionDisplay(int population) {
    ObservationModelList observationModelList = Personality.getCurrentObserved();
//    ObservationModel[] modelArray = observationModelList.toArray(new ObservationModel[observationModelList.size()]);

    String[] stringArray;

    if (observationModelList == null) {
      stringArray = new String[0];
    } else {
      stringArray = new String[observationModelList.size()];
      for (int ii = 0; ii < observationModelList.size(); ii++) {
        ObservationModel model = observationModelList.get(ii);
        stringArray[ii] = model.getSsid();
      }
    }

    if (Personality.getCurrentSortie() == null) {
      textObservationRowCount.setText("Observation Rows:" + Constant.EMPTY);
    } else {
      DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
      long observationPopulation = dataBaseFacade.countObservationRows(true, Personality.getCurrentSortie().getSortieUuid(), getActivity());
      textObservationRowCount.setText("Observation Rows:" + Long.toString(observationPopulation));
    }

    arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, stringArray);
    setListAdapter(arrayAdapter);
  }

  private void updateLocationDisplay() {
    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel != null) {
      String lat = String.format("%.4f", (double) locationModel.getLatitude());
      String lng = String.format("%.4f", (double) locationModel.getLongitude());
      textGeoLoc.setText(lat + " " + lng);

      textLocationTime.setText(TimeUtility.secondsOnly(locationModel.getTimeStamp()));

      if (Personality.getCurrentSortie() == null) {
        textLocationRowCount.setText("Location Rows:" + Constant.EMPTY);
      } else {
        DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
        long locationPopulation = dataBaseFacade.countLocationRows(true, Personality.getCurrentSortie().getSortieUuid(), getActivity());
        textLocationRowCount.setText("Location Rows:" + Long.toString(locationPopulation));
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