package com.digiburo.mellow.heeler.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.digiburo.mellow.heeler.R;

import java.io.Console;

/**
 * React to ActionBar tab events
 */
public class TabHelper implements ActionBar.TabListener, FragmentManager.OnBackStackChangedListener {

  //
  private int backStackCount;

  //
  private MainActivity mainActivity;

  //
  private ActionBar.Tab consoleTab;
  private ActionBar.Tab sortieTab;

  //
  private ChartFragment chartFragment;
  private ConsoleFragment consoleFragment;
  private SortieFragment sortieFragment;

  //
  public static final String LOG_TAG = TabHelper.class.getName();

  public TabHelper(MainActivity activity) {
    mainActivity = activity;
    chartFragment = (ChartFragment) Fragment.instantiate(mainActivity, ChartFragment.class.getName());
    consoleFragment = (ConsoleFragment) Fragment.instantiate(mainActivity, ConsoleFragment.class.getName());
    sortieFragment = (SortieFragment) Fragment.instantiate(mainActivity, SortieFragment.class.getName());
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    if (tab.getTag().equals(ConsoleFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, consoleFragment, ConsoleFragment.FRAGMENT_TAG);
    } else if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, sortieFragment, SortieFragment.FRAGMENT_TAG);
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    fragmentTransaction.remove(chartFragment);

    if (tab.getTag().equals(ConsoleFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(consoleFragment);
    } else  if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(sortieFragment);
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
  }

  /**
   * ActionBar.TabListener
   * @param tab
   * @param fragmentTransaction
   */
  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    fragmentTransaction.remove(chartFragment);

    if (tab.getTag().equals(ConsoleFragment.FRAGMENT_TAG)) {
      //empty
    } else if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, sortieFragment, SortieFragment.FRAGMENT_TAG);
    } else {
      throw new IllegalArgumentException("unknown tab:" + tab.getTag());
    }
  }

  /**
   * FragmentManager.OnBackStackChangedListener
   */
  public void onBackStackChanged() {
    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
    backStackCount = fragmentManager.getBackStackEntryCount();

    System.out.println("backStackCount:" + backStackCount);

/*
    ActionBar actionBar = mainActivity.getActionBar();
    
    if (backStackCount == 0) {
      // restore tabs
      ignoreMe = true;
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    } else {
      // hide tabs
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
*/
  }

  /**
   * populate ActionBar
   */
  public void initialize() {
    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
    fragmentManager.addOnBackStackChangedListener(this);

    ActionBar actionBar = mainActivity.getSupportActionBar();

    consoleTab = actionBar.newTab();
    consoleTab.setTabListener(this);
    consoleTab.setTag(ConsoleFragment.FRAGMENT_TAG);
    consoleTab.setText(R.string.main_tab_console);
    actionBar.addTab(consoleTab);

    sortieTab = actionBar.newTab();
    sortieTab.setTabListener(this);
    sortieTab.setTag(SortieFragment.FRAGMENT_TAG);
    sortieTab.setText(R.string.main_tab_sortie);
    actionBar.addTab(sortieTab);
  }

  /**
   * Map a tag to ActionBar.Tab
   * @param arg
   * @return related tab
   */
  public ActionBar.Tab tagToTab(String arg) {
    if (arg.equals(ConsoleFragment.FRAGMENT_TAG)) {
      return(consoleTab);
    } else if (arg.equals(SortieFragment.FRAGMENT_TAG)) {
      return(sortieTab);
    }

    throw new IllegalArgumentException("unsupported tag:" + arg);
  }

  public void displayGoogleMap(long rowId) {
    chartFragment.setCurrentSortie(rowId);

    FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    Fragment oldFragment = fragmentManager.findFragmentByTag(ChartFragment.FRAGMENT_TAG);
    if (oldFragment != null) {
      fragmentTransaction.remove(oldFragment);
    }

    fragmentTransaction.replace(R.id.layoutFragment01, chartFragment);
//    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 24, 2014 by gsc
 */
