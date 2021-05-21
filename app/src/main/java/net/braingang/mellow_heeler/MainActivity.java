package net.braingang.mellow_heeler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.braingang.mellow_heeler.R;
import net.braingang.mellow_heeler.databinding.ActivityMainBinding;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String LOG_TAG = MainActivity.class.getName();

    private FusedLocationProviderClient fusedLocationClient;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private void permissionDance() {
        Log.i(LOG_TAG, "permissionDance entry");

        int permissionFlag = ContextCompat.checkSelfPermission(this, LocationManager.GPS_PROVIDER);
        Log.i(LOG_TAG, "location perm flag:" + permissionFlag);

        if (PackageManager.PERMISSION_DENIED == permissionFlag) {
            Log.i(LOG_TAG, "must ask location permission");

            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE
            };

            ActivityCompat.requestPermissions(this, permissions, GeoLocService.REQUEST_CODE);
        } else {
            Log.i(LOG_TAG, "location permission granted");
        }

        /*
        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this, LocationManager.GPS_PROVIDER)) {
            Log.i(LOG_TAG, "must get perm");

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION}, GeoLocService.REQUEST_CODE);

            GeoLocService.startGeoLoc(this, false);
        } else {
            Log.i(LOG_TAG, "permission granted");
        }

         */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate onCreate onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        permissionDance();

        /*
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        /*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.i(LOG_TAG, "fresh location");
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(LOG_TAG, location.toString());
                        } else {
                            Log.i(LOG_TAG, "fresh location is null");
                        }
                    }
                });

         */



        /*
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        //GeoLocService.startGeoLoc(this, true);
         */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult:" + requestCode);
        for (String current:permissions) {
            Log.i(LOG_TAG, "permission:" + current);
        }
        for (int current:grantResults) {
            Log.i(LOG_TAG, "grantResult:" + current);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}