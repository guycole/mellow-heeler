package com.digiburo.mellow.heeler.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.SortieModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;

/**
 * React to ActionBar tab events
 */
public class TabHelper implements ActionBar.TabListener, FragmentManager.OnBackStackChangedListener {
  private static final Logger LOG = LoggerFactory.getLogger(TabHelper.class);

  //
  private int backStackCount;

  //
  private MainActivity mainActivity;

  //
  private ActionBar.Tab consoleTab;
  private ActionBar.Tab hotTab;
  private ActionBar.Tab sortieTab;
  private ActionBar.Tab ssidTab;

  //
  private ConsoleFragment consoleFragment;
  private HotFragment hotFragment;
  private SortieFragment sortieFragment;
  private SsidFragment ssidFragment;

  //
  public static final String LOG_TAG = TabHelper.class.getName();

  public TabHelper(MainActivity activity) {
    mainActivity = activity;
    consoleFragment = (ConsoleFragment) Fragment.instantiate(mainActivity, ConsoleFragment.class.getName());
    hotFragment = (HotFragment) Fragment.instantiate(mainActivity, HotFragment.class.getName());
    sortieFragment = (SortieFragment) Fragment.instantiate(mainActivity, SortieFragment.class.getName());
    ssidFragment = (SsidFragment) Fragment.instantiate(mainActivity, SsidFragment.class.getName());
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
    } else if (tab.getTag().equals(HotFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, hotFragment, HotFragment.FRAGMENT_TAG);
    } else if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, sortieFragment, SortieFragment.FRAGMENT_TAG);
    } else if (tab.getTag().equals(SsidFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, ssidFragment, SsidFragment.FRAGMENT_TAG);
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
    if (tab.getTag().equals(ConsoleFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(consoleFragment);
    } else if (tab.getTag().equals(HotFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(hotFragment);
    } else if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(sortieFragment);
    } else if (tab.getTag().equals(SsidFragment.FRAGMENT_TAG)) {
      fragmentTransaction.remove(ssidFragment);
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
    if (tab.getTag().equals(ConsoleFragment.FRAGMENT_TAG)) {
      //empty
    } else if (tab.getTag().equals(HotFragment.FRAGMENT_TAG)) {
      //empty
    } else if (tab.getTag().equals(SortieFragment.FRAGMENT_TAG)) {
      fragmentTransaction.add(R.id.layoutFragment01, sortieFragment, SortieFragment.FRAGMENT_TAG);
    } else if (tab.getTag().equals(SsidFragment.FRAGMENT_TAG)) {
      //empty
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
    LOG.info("backStackCount:" + backStackCount);
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

    ssidTab = actionBar.newTab();
    ssidTab.setTabListener(this);
    ssidTab.setTag(SsidFragment.FRAGMENT_TAG);
    ssidTab.setText(R.string.main_tab_ssid);
    actionBar.addTab(ssidTab);

    hotTab = actionBar.newTab();
    hotTab.setTabListener(this);
    hotTab.setTag(HotFragment.FRAGMENT_TAG);
    hotTab.setText(R.string.main_tab_hot);
    actionBar.addTab(hotTab);
  }

  /**
   * Map a tag to ActionBar.Tab
   * @param arg
   * @return related tab
   */
  public ActionBar.Tab tagToTab(String arg) {
    if (arg.equals(ConsoleFragment.FRAGMENT_TAG)) {
      return consoleTab;
    } else if (arg.equals(HotFragment.FRAGMENT_TAG)) {
      return hotTab;
    } else if (arg.equals(SortieFragment.FRAGMENT_TAG)) {
      return sortieTab;
    } else if (arg.equals(SsidFragment.FRAGMENT_TAG)) {
      return ssidTab;
    }

    throw new IllegalArgumentException("unsupported tag:" + arg);
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 24, 2014 by gsc
 */