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
import com.digiburo.mellow.heeler.lib.TaskController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends ActionBarActivity {
  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

  private Button taskButton;
  private Button uploadButton;

  private boolean taskRun = false;
  private final TaskController taskController = new TaskController();

  /**
   *
   * @param view
   */
  public void onToggleClicked(View view) {
    boolean flag = ((ToggleButton) view).isChecked();

    if (flag) {
      taskRun = true;
      taskController.startTask(getBaseContext());
    } else {
      taskRun = false;
      taskController.stopTask(getBaseContext());
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
        System.out.println("ryryryryry");
        /*
        taskRun = !taskRun;
        if (taskRun) {
          taskButton.setText(R.string.button_stop);
        } else {
          taskButton.setText(R.string.button_start);
        }
        */
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