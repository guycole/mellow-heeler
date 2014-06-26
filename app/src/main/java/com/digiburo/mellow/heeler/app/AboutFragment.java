package com.digiburo.mellow.heeler.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.Personality;

/**
 * Menu option:About, shameless self promotion
 * Implemented as a WebView w/local HTML content
 */
public class AboutFragment extends Fragment {

  //
  public static final String LOG_TAG = AboutFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public AboutFragment() {
    //empty
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_about, container, false);

    String heelerVersion = "Version " + Personality.getApplicationVersion();

    TextView tvHeelerVersion = (TextView) view.findViewById(R.id.tvHeelerVersion);
    tvHeelerVersion.setText(heelerVersion);

    WebView webView = (WebView) view.findViewById(R.id.webView01);
    webView.loadUrl("file:///android_asset/html/about.html");

    return view;
  }
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */
