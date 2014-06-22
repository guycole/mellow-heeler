package com.digiburo.mellow.heeler.app;

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

  private EditText editSortieName;

  private TextView textGeoLoc;
  private TextView textLocationTime;
  private TextView textLocationRowCount;

  private TextView textObservationRowCount;

  private TextView textStatus;
  private ToggleButton toggleStart;

  private MainListener mainListener;

  private ArrayAdapter<String> arrayAdapter;

  /**
   * update display for fresh location or WiFi detection event
   */
  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LOG.info("onReceive noted");

      updateSortieName();
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
    LOG.debug("click:" + position + ":" + id);
  }

  /**
   * delete row (long click)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
    menu.add(0, CONTEXT_ITEM_1, 0, R.string.menu_context_delete);
  }

  /**
   * service context menu
   * @param item
   * @return
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//    LogFacade.entry(LOG_TAG, "on context item select:" + item + ":" + info.id + ":" + arrayAdapter.getItem(info.position));
//    twoListener.createStateDeleteDialog(R.string.alert_delete_title, R.string.alert_delete_message);
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

    textGeoLoc = (TextView) getActivity().findViewById(R.id.textGeoLoc);
    textGeoLoc.setText(Constant.UNKNOWN);

    textLocationTime = (TextView) getActivity().findViewById(R.id.textLocationTime);
    textLocationTime.setText(Constant.UNKNOWN);

    textLocationRowCount = (TextView) getActivity().findViewById(R.id.textLocationRowCount);
    textLocationRowCount.setText(Constant.UNKNOWN);

    textObservationRowCount = (TextView) getActivity().findViewById(R.id.textObservationRowCount);
    textObservationRowCount.setText(Constant.UNKNOWN);

    textStatus = (TextView) getActivity().findViewById(R.id.textStatus);

    toggleStart = (ToggleButton) getActivity().findViewById(R.id.toggleStart);

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
      message = R.string.text_collection;
      colorBackground = Color.GREEN;
      colorText = Color.RED;

      toggleStart.setChecked(true);
      LOG.debug("start collection true");
    } else {
      toggleStart.setChecked(false);
      LOG.debug("start collection false");

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

    arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stringArray);
    setListAdapter(arrayAdapter);

    if (Personality.getCurrentSortie() == null) {
      textObservationRowCount.setText("Empty");
    } else {
      DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
      int observationPopulation = dataBaseFacade.countObservationRows(Personality.getCurrentSortie().getSortieUuid(), getActivity());
      textObservationRowCount.setText(Integer.toString(observationPopulation));
    }
  }

  private void updateLocationDisplay() {
    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel != null) {
      String lat = String.format("%.4f", (double) locationModel.getLatitude());
      String lng = String.format("%.4f", (double) locationModel.getLongitude());
      textGeoLoc.setText(lat + " " + lng);

      // remove fractions of second
      String tempTime = locationModel.getTimeStamp();
      int ndx = tempTime.lastIndexOf(".");
      tempTime = tempTime.substring(0, ndx) + "Z";

      textLocationTime.setText(tempTime);

      if (Personality.getCurrentSortie() == null) {
        textLocationRowCount.setText("Empty");
      } else {
        DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
        int locationPopulation = dataBaseFacade.countLocationRows(Personality.getCurrentSortie().getSortieUuid(), getActivity());
        textLocationRowCount.setText(Integer.toString(locationPopulation));
      }
    }
  }

  private void updateSortieName() {
    String temp = editSortieName.getText().toString().trim();
    if (temp.isEmpty()) {
      return;
    }

    if (Personality.getCurrentSortie() == null) {
      return;
    }

    if (temp.equals(Personality.getCurrentSortie().getSortieName())) {
      return;
    }

    SortieModel sortieModel = Personality.getCurrentSortie();
    sortieModel.setSortieName(temp);

    DataBaseFacade dataBaseFacade = new DataBaseFacade(getActivity());
    dataBaseFacade.updateSortie(sortieModel, getActivity());
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