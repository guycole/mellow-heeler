package com.digiburo.mellow.heeler.app;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.digiburo.mellow.heeler.R;

/**
 * React to ActionBar tab events
 */
public class TabHelper implements ActionBar.TabListener, FragmentManager.OnBackStackChangedListener {

  //
  private boolean ignoreMe = false;

  //
  private MainActivity mainActivity;

  //
  private ActionBar.Tab chartTab;
  private ActionBar.Tab splashTab;
  private ActionBar.Tab stationListTab;

  //
  private ChartFragment chartFragment;
//  private ObservationListFragment observationListFragment;
//  private SplashFragment splashFragment;
//  private StationListFragment stationListFragment;

  //
  public static final String LOG_TAG = TabHelper.class.getName();

  public TabHelper(MainActivity activity) {
    mainActivity = activity;

//    chartFragment = (ChartFragment) Fragment.instantiate(mainActivity, ChartFragment.class.getName());
//    splashFragment = (SplashFragment) Fragment.instantiate(mainActivity, SplashFragment.class.getName());
//    stationListFragment = (StationListFragment) Fragment.instantiate(mainActivity, StationListFragment.class.getName());
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    if (ignoreMe) {
      //spurious event caused by changing navigation mode within onStateDeselect()
      ignoreMe = false;
      return;
    }

    /*
    if (tab.getTag().equals(TAG_CHART)) {
      fragmentTransaction.add(R.id.layoutFragment01, chartFragment, TAG_CHART);
    } else if (tab.getTag().equals(TAG_SPLASH)) {
      fragmentTransaction.add(R.id.layoutFragment01, splashFragment, TAG_SPLASH);
    } else if (tab.getTag().equals(TAG_STATION_LIST)) {
      fragmentTransaction.add(R.id.layoutFragment01, stationListFragment, TAG_STATION_LIST);
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
    */
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    /*
    if (tab.getTag().equals(TAG_CHART)) {
      fragmentTransaction.remove(chartFragment);
    } else  if (tab.getTag().equals(TAG_SPLASH)) {
      fragmentTransaction.remove(splashFragment);
    } else if (tab.getTag().equals(TAG_STATION_LIST)) {
      fragmentTransaction.remove(stationListFragment);
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
    */
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    /*
    if (tab.getTag().equals(TAG_CHART)) {
      //empty
    } else if (tab.getTag().equals(TAG_SPLASH)) {
      //empty
    } else if (tab.getTag().equals(TAG_STATION_LIST)) {
      //empty
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
    */
  }

  /**
   * FragmentManager.OnBackStackChangedListener
   */
  public void onBackStackChanged() {
    FragmentManager fragmentManager = mainActivity.getFragmentManager();
    int backStackCount = fragmentManager.getBackStackEntryCount();
    
    ActionBar actionBar = mainActivity.getActionBar();
    
    if (backStackCount == 0) {
      // restore tabs
      ignoreMe = true;
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    } else {
      // hide tabs
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
  }

  /**
   * populate ActionBar
   */
  public void initialize() {
    FragmentManager fragmentManager = mainActivity.getFragmentManager();
    fragmentManager.addOnBackStackChangedListener(this);

    ActionBar actionBar = mainActivity.getActionBar();

    chartTab = actionBar.newTab();
    splashTab = actionBar.newTab();
    stationListTab = actionBar.newTab();

    chartTab.setTabListener(this);
    splashTab.setTabListener(this);
    stationListTab.setTabListener(this);

    /*
    chartTab.setTag(TAG_CHART);
    splashTab.setTag(TAG_SPLASH);
    stationListTab.setTag(TAG_STATION_LIST);

    chartTab.setText(R.string.menu_application_bar_map);
    splashTab.setText(R.string.menu_application_bar_splash);
    stationListTab.setText(R.string.menu_application_bar_station_list);

    actionBar.addTab(splashTab);
    actionBar.addTab(stationListTab);
    actionBar.addTab(chartTab);

    LogFacade.exit(LOG_TAG, "initialize");
    */
  }

  /**
   * Map a tag to ActionBar.Tab
   * @param arg
   * @return related tab
   */
  public ActionBar.Tab tagToTab(String arg) {
    /*
    if (arg.equals(TAG_CHART)) {
      return(chartTab);
    } else if (arg.equals(TAG_SPLASH)) {
      return(splashTab);
    } else if (arg.equals(TAG_STATION_LIST)) {
      return(stationListTab);
    }
    */

    throw new IllegalArgumentException("unsupported tag:" + arg);
  }
}

/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */
