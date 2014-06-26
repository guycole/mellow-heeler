package com.digiburo.mellow.heeler.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.HotModel;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.utility.StringList;
import com.digiburo.mellow.heeler.lib.utility.UserPreferenceHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends ActionBarActivity implements MainListener {
  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

  private TabHelper tabHelper;

  private LocationDialog locationDialog;
  private ObservationDialog observationDialog;
  private SortieDialog sortieDialog;

  private final SortieController sortieController = new SortieController();

  // user preference changes require the sortie to be restarted
  private SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      LOG.debug("shared preference change:" + key);

      if (UserPreferenceHelper.USER_PREF_POLL_FREQUENCY.equals(key)) {
        LOG.debug("wifi polling update");
        sortieStop();
      } else if (UserPreferenceHelper.USER_PREF_POLL_DISTANCE.equals(key)) {
        LOG.debug("distance update");
        sortieStop();
      }
    }
  };

  /**
   * mainListener
   * @param observationModel
   */
  @Override
  public void addHot(ObservationModel observationModel) {
    HotModel hotModel = new HotModel();
    hotModel.setDefault();

    hotModel.setBssid(observationModel.getBssid());
    hotModel.setSsid(observationModel.getSsid());

    DataBaseFacade dataBaseFacade = new DataBaseFacade(this);
    dataBaseFacade.insert(hotModel, this);
  }

  /**
   * mainListener
   * @param locationModel
   */
  @Override
  public void displayGoogleMap(LocationModel locationModel) {
    Intent intent = new Intent(this, ChartActivity.class);
    intent.putExtra(Constant.INTENT_LOCATION_UUID, locationModel.getLocationUuid());
    startActivity(intent);
  }

  /**
   * mainListener
   * @param observationModel
   */
  @Override
  public void displayGoogleMap(ObservationModel observationModel) {
    Intent intent = new Intent(this, ChartActivity.class);
    intent.putExtra(Constant.INTENT_OBSERVATION_UUID, observationModel.getObservationUuid());
    startActivity(intent);
  }

  /**
   * mainListener
   * @param sortieModel
   */
  @Override
  public void displayGoogleMap(SortieModel sortieModel) {
    Intent intent = new Intent(this, ChartActivity.class);
    intent.putExtra(Constant.INTENT_SORTIE_UUID, sortieModel.getSortieUuid());
    startActivity(intent);
  }

  /**
   * mainListener
   * display location detail
   * @param uuid
   */
  public void displayLocationDetail(String uuid) {
    LOG.info("displayLocationDetail:" + uuid);

    FragmentTransaction fragmentTransaction = prepareFragmentTransaction();

    locationDialog = LocationDialog.newInstance(uuid);
    locationDialog.show(fragmentTransaction, LocationDialog.FRAGMENT_TAG);
  }

  /**
   * mainListener
   * display observation detail
   * @param uuid
   */
  public void displayObservationDetail(String uuid) {
    LOG.info("displayObservationDetail:" + uuid);

    FragmentTransaction fragmentTransaction = prepareFragmentTransaction();

    observationDialog = ObservationDialog.newInstance(uuid);
    observationDialog.show(fragmentTransaction, ObservationDialog.FRAGMENT_TAG);
  }

  /**
   *  mainListener
   * display sortie detail
   * @param uuid
   */
  public void displaySortieDetail(String uuid) {
    LOG.info("displaySortieDetail:" + uuid);

    FragmentTransaction fragmentTransaction = prepareFragmentTransaction();

    sortieDialog = SortieDialog.newInstance(uuid);
    sortieDialog.show(fragmentTransaction, ObservationDialog.FRAGMENT_TAG);
  }

  /**
   *
   * @return
   */
  private FragmentTransaction prepareFragmentTransaction() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    Fragment previous = fragmentManager.findFragmentByTag(ObservationDialog.FRAGMENT_TAG);
    if (previous != null) {
      fragmentTransaction.remove(previous);
    }

 //   fragmentTransaction.addToBackStack(null);

    return fragmentTransaction;
  }

  /**
   * mainListener
   */
  @Override
  public void sortieStart(final String sortieName) {
    LOG.info("sortieStart");
    sortieController.startSortie(sortieName, this);
  }

  /**
   * mainListener
   */
  @Override
  public void sortieStop() {
    LOG.info("sortieStop");
    sortieController.stopSortie(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    tabHelper = new TabHelper(this);
    tabHelper.initialize();

    setupNotifier();

    // some user preference changes require the sortie to be restarted
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.registerOnSharedPreferenceChangeListener(sharedPrefListener);
  }

  private void setupNotifier() {
    Intent intent = new Intent(this, MainActivity.class);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(intent);

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentTitle("Mellow Heeler");
    builder.setContentText("Notification Placeholder");
    builder.setContentIntent(pendingIntent);
    builder.setSmallIcon(R.drawable.icon24);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(2718, builder.build());
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
      case R.id.menu_settings:
        intent.setAction(Constant.INTENT_ACTION_SETTING);
        break;
      case R.id.menu_upload:
        sortieStop();
        intent.setAction(Constant.INTENT_ACTION_UPLOAD);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    startActivity(intent);
    return true;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */