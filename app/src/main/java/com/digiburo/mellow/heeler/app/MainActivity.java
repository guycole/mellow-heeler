package com.digiburo.mellow.heeler.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.SortieController;
import com.digiburo.mellow.heeler.lib.UploadController;
import com.digiburo.mellow.heeler.lib.content.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.content.SortieModel;
import com.digiburo.mellow.heeler.lib.network.NetworkFacade;
import com.digiburo.mellow.heeler.lib.network.RemoteConfiguration;
import com.digiburo.mellow.heeler.lib.utility.TimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends ActionBarActivity {
  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

  private Button taskButton;
  private Button uploadButton;

  private boolean sortieRun = false;
  private final SortieController sortieController = new SortieController();
  private final UploadController uploadController = new UploadController();

  /**
   *
   * @param view
   */
  public void onToggleClicked(View view) {
    boolean flag = ((ToggleButton) view).isChecked();

    if (flag) {
      sortieRun = true;
      sortieController.startSortie(getBaseContext());
    } else {
      sortieRun = false;
      sortieController.stopSortie(getBaseContext());
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    uploadButton = (Button) findViewById(R.id.buttonUpload);
    uploadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        uploadController.uploadAll(getBaseContext());
        NetworkFacade networkFacade = new NetworkFacade();
        networkFacade.readRemoteConfiguration(getBaseContext());
      }
    });
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
      case R.id.menu_preference:
        intent.setAction(Constant.INTENT_ACTION_PREFERENCE);
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