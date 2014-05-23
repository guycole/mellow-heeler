package com.digiburo.mellow.heeler.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;
import com.digiburo.mellow.heeler.lib.SortieController;
import com.digiburo.mellow.heeler.lib.UploadController;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends ActionBarActivity {
  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

  private Button buttonSpecialLocation;

  private EditText editSortieName;

  private TextView textLastLocation;
  private TextView textLastLocationTime;
  private TextView textLocationRowCount;
  private TextView textLastSsid;
  private TextView textLastSsidTime;
  private TextView textObservationRowCount;
  private TextView textStatus;

  private ToggleButton buttonStart;

  private boolean sortieRun = false;
  private final SortieController sortieController = new SortieController();
  private final UploadController uploadController = new UploadController();

  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      LOG.info("onReceive noted");

      long rowKey = intent.getLongExtra(Constant.INTENT_ROW_KEY, 0);
      if (rowKey < 1) {
        updateLocationDisplay();
      } else {
        updateDetectionDisplay(rowKey);
      }
    }
  };

  private SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    LOG.debug("shared preference change:" + key);

    if (UserPreferenceHelper.USER_PREF_POLL_FREQUENCY.equals(key)) {
      LOG.debug("wifi polling update");
      stopCollection();
    } else if (UserPreferenceHelper.USER_PREF_POLL_DISTANCE.equals(key)) {
      LOG.debug("distance update");
      stopCollection();
    }
    }
  };

  /**
   * Collection might need to stop because of user preference changes or database uploads.
   * This implies the user preference display is active (and not main activity).
   * Stop collection and force the start/stop button to reset.
   * Display will update when onResume is invoked.
   */
  private void stopCollection() {
    if (buttonStart.isChecked()) {
      buttonStart.setChecked(false);
      sortieRun = false;
      sortieController.stopSortie(this);
    }
  }

  /**
   *
   * @param view
   */
  public void onToggleClicked(final View view) {
    if (buttonStart.isChecked()) {
      LOG.info("sortieRun true");
      sortieRun = true;
      sortieController.startSortie(editSortieName.getText().toString(), this);
    } else {
      LOG.info("sortieRun false");
      sortieRun = false;
      sortieController.stopSortie(this);
    }

    updateStatusLabel();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    buttonSpecialLocation = (Button) findViewById(R.id.buttonSpecialLocation);
    buttonSpecialLocation.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        LocationModel locationModel = Personality.getCurrentLocation();
        if (locationModel == null) {
          LOG.debug("unable to update null location");
        } else {
          LOG.debug("updating current location");
          locationModel.setSpecialFlag();
          DataBaseFacade dataBaseFacade = new DataBaseFacade(getBaseContext());
          dataBaseFacade.updateLocation(locationModel, getBaseContext());
        }
      }
    });

    buttonStart = (ToggleButton) findViewById(R.id.buttonStart);

    editSortieName = (EditText) findViewById(R.id.editSortieName);

    textLastLocation = (TextView) findViewById(R.id.textLastLocation);
    textLastLocation.setText(Constant.UNKNOWN);

    textLastLocationTime = (TextView) findViewById(R.id.textLastLocationTime);
    textLastLocationTime.setText(Constant.UNKNOWN);

    textLocationRowCount = (TextView) findViewById(R.id.textLocationRowCount);
    textLocationRowCount.setText(Constant.UNKNOWN);

    textLastSsid = (TextView) findViewById(R.id.textLastSsid);
    textLastSsid.setText(Constant.UNKNOWN);

    textLastSsidTime = (TextView) findViewById(R.id.textLastSsidTime);
    textLastSsidTime.setText(Constant.UNKNOWN);

    textObservationRowCount = (TextView) findViewById(R.id.textObservationRowCount);
    textObservationRowCount.setText(Constant.UNKNOWN);

    textStatus = (TextView) findViewById(R.id.textStatus);

    // some user preference changes require the sortie to be restarted
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.registerOnSharedPreferenceChangeListener(sharedPrefListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateStatusLabel();
    updateDetectionDisplay(0);
    updateLocationDisplay();
    registerReceiver(broadcastReceiver, new IntentFilter(Constant.FRESH_UPDATE));
  }

  @Override
  public void onPause() {
    super.onPause();
    unregisterReceiver(broadcastReceiver);
  }

  /**
   * invoked during device orientation change
   * @param outState
   */
  @Override
  public void onSaveInstanceState(Bundle outState) {
    /*
    LogFacade.entry(LOG_TAG, "onSaveInstanceState");
    outState.putInt(SELECTED_TAB_NDX, getActionBar().getSelectedNavigationIndex());
    outState.putLong(SELECTED_STATION_ID, selectedStationId);
    outState.putLong(DELETE_STATION_ID, deleteStationId);
    */
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent = new Intent(this, MenuActivity.class);

    switch (item.getItemId()) {
      case R.id.menu_about:
        intent.setAction(Constant.INTENT_ACTION_ABOUT);
        break;
      case R.id.menu_clean:
        intent.setAction(Constant.INTENT_ACTION_CLEAN);
        break;
      case R.id.menu_preference:
        intent.setAction(Constant.INTENT_ACTION_PREFERENCE);
        break;
      case R.id.menu_upload:
        stopCollection();
        intent.setAction(Constant.INTENT_ACTION_UPLOAD);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    startActivity(intent);
    return(true);
  }

  /**
   * manage status label
   */
  private void updateStatusLabel() {
    int colorBackground = Color.RED;
    int colorText = Color.GREEN;
    int message = R.string.text_idle;

    if (sortieRun) {
      message = R.string.text_collection;
      colorBackground = Color.GREEN;
      colorText = Color.RED;
    }

    textStatus.setText(message);
    textStatus.setBackgroundColor(colorBackground);
    textStatus.setTextColor(colorText);
  }

  private void updateDetectionDisplay(long rowKey) {
    DataBaseFacade dataBaseFacade = new DataBaseFacade(this);
    int observationPopulation = dataBaseFacade.countObservationRows(this);

    String ssid = "Unknown";
    String timeStamp = "Unknown";

    if (rowKey > 0) {
      ObservationModel observationModel = dataBaseFacade.selectObservation(rowKey, this);
      ssid = observationModel.getSsid();
      timeStamp = observationModel.getTimeStamp();
    }

    textLastSsid.setText(ssid);
    textLastSsidTime.setText(timeStamp);
    textObservationRowCount.setText(Integer.toString(observationPopulation));
  }

  private void updateLocationDisplay() {
    DataBaseFacade dataBaseFacade = new DataBaseFacade(this);

    LocationModel locationModel = Personality.getCurrentLocation();
    if (locationModel != null) {
      String latString = String.format("%.6f", (double) locationModel.getLatitude());
      String lngString = String.format("%.6f", (double) locationModel.getLongitude());
      textLastLocation.setText(latString + ", " + lngString);
      textLastLocationTime.setText(locationModel.getTimeStamp());

      int locationPopulation = dataBaseFacade.countLocationRows(this);
      textLocationRowCount.setText(Integer.toString(locationPopulation));
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */