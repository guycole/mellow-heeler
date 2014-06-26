package com.digiburo.mellow.heeler.app;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.LocationModelList;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.digiburo.mellow.heeler.lib.database.SortieModelList;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
*/

/**
 * Service the "map" tab - display google maps
 * Attempt to display all known stations
 * Center on favorite station if defined
 */
public class ChartFragment extends SupportMapFragment {
  private static final Logger LOG = LoggerFactory.getLogger(ChartFragment.class);
  public static final String FRAGMENT_TAG = "TAG_CHART";

  LocationModel locationModel;
  ObservationModel observationModel;
  private SortieModel sortieModel;

  DataBaseFacade dataBaseFacade;
  /////

  private long rowId;

  private LocationModelList locationModelList;
  private ObservationModelList observationModelList;

  /**
   * mandatory empty ctor
   */
  public ChartFragment() {
    //empty
  }

  /*
  public void setCurrentSortie(long rowId) {
    this.rowId = rowId;
  }
  */


  public void setCurrentLocation(LocationModel locationModel) {
    this.locationModel = locationModel;
    this.observationModel = null;
    this.sortieModel = null;
  }

  public void setCurrentObservation(ObservationModel observationModel) {
    this.locationModel = null;
    this.observationModel = observationModel;
    this.sortieModel = null;
  }

  public void setCurrentSortie(SortieModel sortieModel) {
    this.locationModel = null;
    this.observationModel = null;
    this.sortieModel = sortieModel;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    dataBaseFacade = new DataBaseFacade(activity);

    sortieModel = dataBaseFacade.selectSortie(rowId, activity);
    LOG.debug("sortie:" + rowId + ":" + sortieModel.getSortieName() + ":" + sortieModel.getSortieUuid());
    locationModelList = dataBaseFacade.selectAllLocations(true, sortieModel.getSortieUuid(), 0, activity);
    LOG.debug("total locations:" + locationModelList.size());
    observationModelList = dataBaseFacade.selectAllObservations(true, sortieModel.getSortieUuid(), 0, activity);
    LOG.debug("total observations:" + observationModelList.size());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);

    UiSettings settings = getMap().getUiSettings();
    settings.setAllGesturesEnabled(false);
    settings.setMyLocationButtonEnabled(false);

    if (locationModel != null) {
      LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
//      getMap().addMarker(new MarkerOptions().position(latLng).title(model.getIdentifier()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
      getMap().addMarker(new MarkerOptions().position(latLng).title("xx").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
      getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    } else if (observationModel != null) {
      LocationModel locationModel = dataBaseFacade.selectLocation(observationModel.getLocationUuid(), getActivity());

      LatLng latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());
//      getMap().addMarker(new MarkerOptions().position(latLng).title(model.getIdentifier()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
      getMap().addMarker(new MarkerOptions().position(latLng).title("xx").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker)));
      getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    } else if (sortieModel != null) {
      //TODO
      System.out.println("start here");
    }

    /*
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
  
    return view;
  }

  /*
  //
  private ArrayList<StationModel> stationList;
  private long favoriteStationRowId;
*/

  /*

  @Override
  public void onStop() {
    super.onStop();

    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    Fragment oldFragment = fragmentManager.findFragmentByTag(ChartFragment.FRAGMENT_TAG);
    if (oldFragment != null) {
      fragmentTransaction.remove(oldFragment);
    }

//    fragmentTransaction.replace(R.id.layoutFragment01, chartFragment);
    fragmentTransaction.commit();
  }
  */
}
/*
 * Copyright 2014 Digital Burro, INC
 * Created on May 12, 2014 by gsc
 */
