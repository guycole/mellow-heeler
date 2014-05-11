package com.digiburo.mellow.heeler.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.TaskController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainActivity extends ActionBarActivity {

  private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

  private Button taskButton;
  private Button uploadButton;

  private boolean taskRun = false;
  private final TaskController taskController = new TaskController();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    taskButton = (Button) findViewById(R.id.buttonTask);
    taskButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        taskRun = !taskRun;
        if (taskRun) {
          taskButton.setText(R.string.button_stop);
          taskController.startTask(getBaseContext());
        } else {
          taskButton.setText(R.string.button_start);
          taskController.stopTask(getBaseContext());
        }
      }
    });

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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 10, 2014 by gsc
 */