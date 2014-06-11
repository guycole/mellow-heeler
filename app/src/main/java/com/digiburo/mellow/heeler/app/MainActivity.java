package com.digiburo.mellow.heeler.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.SortieController;
import com.digiburo.mellow.heeler.lib.UploadController;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends ActionBarActivity implements MainListener {
  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);
  private TabHelper tabHelper;

  private final SortieController sortieController = new SortieController();
  private final UploadController uploadController = new UploadController();

  // user preference changes require the sortie to be restarted
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
   * start location/WiFi collection
   */
  public void startCollection(final String sortieName) {
    LOG.info("start collection");

    if (sortieName == null) {
      sortieController.startSortie(Constant.DEFAULT_SORTIE_NAME, this);
    } else {
      sortieController.startSortie(sortieName, this);
    }
  }

  /**
   * stop location/WiFi collection
   */
  public void stopCollection() {
    LOG.info("stop collection");
    sortieController.stopSortie(this);
  }

  /**
   * service start/stop toggle button
   * @param view
   */
  public void onToggleClicked(final View view) {
    boolean flag = ((ToggleButton) view).isChecked();
    LOG.info("toggle:" + flag);

    if (flag) {
      startCollection(null);
    } else {
      stopCollection();
    }
  }

  /**
   * switch sortie list for google map
   */
  @Override
  public void displayGoogleMap(long rowId) {
    tabHelper.displayGoogleMap(rowId);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    tabHelper = new TabHelper(this);
    tabHelper.initialize();

    // some user preference changes require the sortie to be restarted
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.registerOnSharedPreferenceChangeListener(sharedPrefListener);
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
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */