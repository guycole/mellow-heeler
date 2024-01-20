package net.braingang.heeler;

import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

/**
 * Write observation for each location update;
 */
public class GeoLocService extends IntentService {
    public static final String LOG_TAG = GeoLocService.class.getName();

    public static final int REQUEST_CODE = 6789;

    public static final float GEO_MIN_DISTANCE = Constant.ONE_KILOMETER;
    public static final long GEO_MIN_TIME = Constant.ONE_MINUTE;

    /*
    public static final long BLE_SCAN_DURATION = 3333L;

    private Boolean bleScanComplete = false;
    private Handler bleScanHandler = new Handler();

    private FileFacade fileFacade = new FileFacade();
    private Observation observation;
    private UserPreferenceHelper userPreferenceHelper = new UserPreferenceHelper();
*/

    public GeoLocService() {
        super("GeoLocService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "xxx xxx onCreate xxx xxx");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "xxx xxx onDestroy xxx xxx");
    }

    /**
     * start geoloc service
     * @param context
     * @param singleFlag true, perform only one collection cycle else run until cancelled
     */
    public static void startGeoLoc(Context context, boolean singleFlag) {
        Log.i(LOG_TAG, "start geoloc start geoloc start geoloc");

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (Personality.geoLocPending != null) {
            Log.i(LOG_TAG, "overwrite non null pending intent");
        }

        Intent intent = new Intent(context, GeoLocService.class);
        Personality.geoLocPending = PendingIntent.getService(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (singleFlag) {
            Log.i(LOG_TAG, "aaaaaa");
            locationManager.requestSingleUpdate(criteria, Personality.geoLocPending);
        } else {
            Log.i(LOG_TAG, "bbbbbb");
            locationManager.requestLocationUpdates(GEO_MIN_TIME, GEO_MIN_DISTANCE, criteria, Personality.geoLocPending);
        }
    }

    /**
     * stop geoloc service
     * @param context
     */
    public static void stopGeoLoc(Context context) {
        if (Personality.geoLocPending == null) {
            Log.i(LOG_TAG, "unable to stop w/null pending intent");
        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(Personality.geoLocPending);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");

        Bundle bundle = intent.getExtras();


        if ((bundle != null) && (bundle.containsKey(LocationManager.KEY_LOCATION_CHANGED))) {
            Log.i(LOG_TAG, "aaaaaaaaaa");

            /*
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phone1 = telephonyManager.getLine1Number();
            String operator = telephonyManager.getNetworkOperator();
            String operatorName = telephonyManager.getNetworkOperatorName();
            */

            //observation = new Observation(phone1, operator, operatorName);

            Location location = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);
            Log.i(LOG_TAG, location.toString());

            freshLocation(location);
/*
            if (userPreferenceHelper.isBleCollection(this)) {
                collectBle();

                while (!bleScanComplete) {
                    try {
                        Thread.sleep(Constant.THIRTY_SECOND);
                    } catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } else {
                Log.i(LOG_TAG, "BLE collection disabled");
            }
*/
            /*
            if (userPreferenceHelper.isCellularCollection(this)) {
                collectCellular();
            } else {
                Log.i(LOG_TAG, "Cellular collection disabled");
            }

             */

            /*
            if (userPreferenceHelper.isWiFiCollection(this)) {
                collectWiFi();
            } else {
                Log.i(LOG_TAG, "WiFi collection disabled");
            }

            fileFacade.writeObservation(observation, this);

             */
        } else {
            Log.i(LOG_TAG, "skipping intent w/missing location");
        }
    }

    /*
    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            observation.addBleObservation(device, rssi, scanRecord);
        }
    };

     */
/*
    private void collectBle() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter btAdapter = manager.getAdapter();

        if (btAdapter == null) {
            Log.i(LOG_TAG, "null bluetooth adapter");
            return;
        }

        if (btAdapter.isEnabled()) {
            btAdapter.startLeScan(bleScanCallback);

            bleScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (btAdapter != null) {
                        btAdapter.stopLeScan(bleScanCallback);
                    }

                    bleScanComplete = true;
                }
            }, BLE_SCAN_DURATION);
        } else {
            Log.i(LOG_TAG, "bluetooth adapter disabled");
        }
    }

 */

    /*
    private void collectCellular() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> cellList = telephonyManager.getAllCellInfo();

        observation.addCellular(cellList);
    }

     */

    /*
    private void collectWiFi() {
        observation.addWiFi();
    }
     */

    /*
    private void saveFreshLocation(Location location) {
        Personality.currentLocation = location;
        observation.setLocation(location);
    }

     */

    private void freshLocation(Location location) {
        Log.i(LOG_TAG, "fresh location noted:" + location.getProvider() + ":" + location.getTime());

        /*
        if (Personality.currentLocation == null) {
            Log.i(LOG_TAG, "null current");
            saveFreshLocation(location);
        }

        if ((Personality.currentLocation.getTime() == location.getTime()) && (Personality.currentLocation.getProvider().equals(location.getProvider()))) {
            Log.i(LOG_TAG, "current location match");
        } else {
            Log.i(LOG_TAG, "current location fail");
            saveFreshLocation(location);
        }
         */
    }
}
