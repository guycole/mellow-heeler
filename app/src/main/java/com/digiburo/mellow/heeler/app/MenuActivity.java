package com.digiburo.mellow.heeler.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.UploadController;

/**
 * support for menu selection
 */
public class MenuActivity extends Activity {
  private static final Logger LOG = LoggerFactory.getLogger(MenuActivity.class);


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    ActionBar actionBar = getActionBar();
    actionBar.setHomeButtonEnabled(true);

    Intent intent = getIntent();
    if (intent == null) {
      finish();
    } else {
      Fragment fragment = null;

      String action = intent.getAction();
      if (Constant.INTENT_ACTION_ABOUT.equals(action)) {
        fragment = new AboutFragment();
      } else if (Constant.INTENT_ACTION_CLEAN.equals(action)) {
        fragment = new CleanFragment();
      } else if (Constant.INTENT_ACTION_PREFERENCE.equals(action)) {
        fragment = new PrefFragment();
      } else if (Constant.INTENT_ACTION_UPLOAD.equals(action)) {
        fragment = new UploadFragment();
      } else {
        LOG.error("onConfigurationChanged");
        finish();
      }

      if (fragment != null) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.layoutFragment01, fragment);
        fragmentTransaction.commit();
      }
    }
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */
