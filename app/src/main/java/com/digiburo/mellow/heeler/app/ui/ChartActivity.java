package com.digiburo.mellow.heeler.app.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.digiburo.mellow.heeler.R;
import com.digiburo.mellow.heeler.lib.Constant;
import com.digiburo.mellow.heeler.lib.database.DataBaseFacade;
import com.digiburo.mellow.heeler.lib.database.LocationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModel;
import com.digiburo.mellow.heeler.lib.database.ObservationModelList;
import com.digiburo.mellow.heeler.lib.database.SortieModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartActivity extends ActionBarActivity {
  private static final Logger LOG = LoggerFactory.getLogger(ChartActivity.class);

  private LocationModel locationModel;
  private ObservationModel observationModel;
  private SortieModel sortieModel;

  private ObservationModelList observationModelList;

  private DataBaseFacade dataBaseFacade;

  private GoogleMap googleMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chart);

    googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
    googleMap.setMyLocationEnabled(true);

    dataBaseFacade = new DataBaseFacade(this);

    Intent intent = getIntent();
    String locationUuid = intent.getStringExtra(Constant.INTENT_LOCATION_UUID);
    String observationUuid = intent.getStringExtra(Constant.INTENT_OBSERVATION_UUID);
    String sortieUuid = intent.getStringExtra(Constant.INTENT_SORTIE_UUID);

    LatLng latLng = null;
    if (locationUuid != null) {
      locationModel = dataBaseFacade.selectLocation(locationUuid, this);
      latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());

      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
      googleMap.addMarker(new MarkerOptions().title(locationModel.getTimeStamp()).position(latLng));
    } else if (observationUuid != null) {
      observationModel = dataBaseFacade.selectObservation(observationUuid, this);
      locationModel = dataBaseFacade.selectLocation(observationModel.getLocationUuid(), this);
      latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());

      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
      googleMap.addMarker(new MarkerOptions().title(observationModel.getSsid()).position(latLng));
    } else if (sortieUuid != null) {
      boolean firstFlag = true;
      sortieModel = dataBaseFacade.selectSortie(sortieUuid, this);
      observationModelList = dataBaseFacade.selectAllObservations(true, sortieModel.getSortieUuid(), 0, this);
      for (ObservationModel observationModel:observationModelList) {
        locationModel = dataBaseFacade.selectLocation(observationModel.getLocationUuid(), this);
        latLng = new LatLng(locationModel.getLatitude(), locationModel.getLongitude());

        if (firstFlag) {
          firstFlag = false;
          googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }

        googleMap.addMarker(new MarkerOptions().title(observationModel.getSsid()).position(latLng));
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chart, menu);
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
