package com.digiburo.mellow.heeler.app;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Service the "map" tab - display google maps
 * Attempt to display all known stations
 * Center on favorite station if defined
 */
public class ChartFragment extends MapFragment {

  //
  public static final String LOG_TAG = ChartFragment.class.getName();

  /**
   * mandatory empty ctor
   */
  public ChartFragment() {
    //empty
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    /*
    LogFacade.entry(LOG_TAG, "onAttach");
    
    DataBaseFacade dbf = new DataBaseFacade(activity);
    stationList = dbf.getActiveStationModels();
    
    UserPreferenceHelper uph = new UserPreferenceHelper();
    favoriteStationRowId = uph.getFaveStation(activity);
    */
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//    LogFacade.entry(LOG_TAG, "onCreateView");
    
    View view = super.onCreateView(inflater, container, savedInstanceState);

    /*
    UiSettings settings = getMap().getUiSettings();
//    settings.setAllGesturesEnabled(false);
    settings.setMyLocationButtonEnabled(false);
    
    StationModel originModel = null;
 
    if ((stationList != null) && (!stationList.isEmpty())) {
      for (StationModel model:stationList) {
        if (model.getId().longValue() == favoriteStationRowId) {
          originModel = model;
        }
        
        LatLng latLng = new LatLng(model.getLatitude(), model.getLongitude());
        getMap().addMarker(new MarkerOptions().position(latLng).title(model.getIdentifier()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
      }
    }
    
    if (originModel == null) {
      if ((stationList != null) && (!stationList.isEmpty())) {
        originModel = stationList.get(0);
      }
    }
    
    if (originModel != null) {
      LatLng latLng = new LatLng(originModel.getLatitude(), originModel.getLongitude());
      getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
    */
  
    return(view);
  }

  /*
  //
  private ArrayList<StationModel> stationList;
  private long favoriteStationRowId;
*/
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */
