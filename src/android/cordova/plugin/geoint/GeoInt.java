package cordova.plugin.geoint;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.telecom.Call;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cordova.plugin.geoint.domain.Position;
import cordova.plugin.geoint.domain.SensorItem;

/**
 * Main class for collecting sensor data.
 *
 * @see https://developers.google.com/web/updates/2017/09/sensors-for-the-web
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    private GeoLocationListener mLocationListener;
    private GeoSensorListener mSensorListener;
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;
    private CallbackContext mCallbackContext;

    private static final int GPS_POSITION  = 1;

    private final static String CALLBACK_CONTEXT = "callback_context";

    private final static String ESDERS_IR_URL = "market://details?id=de.esders.ir";
    /**
     *
     */
    private final static int RESULT_READ_JSON = 1;

    /**
     * Boolean which selects the readout by JSON
     */
    public static final String EXTRA_READ_JSON = "read_json";

    /**
     * Integer which selects the measurement for reading 0: all measurements,
     * -1: only the last measurement, >0: one special measurement
     */
    public static final String EXTRA_READ_JSON_NUMBER = "read_json_number";


    // Network provider for low battery usage
    private String mProvider = LocationManager.NETWORK_PROVIDER;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "execute initialize");
        super.initialize(cordova, webView);
        mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // TODO test logcat error printing (system shell: adb logcat -s GEO)
        Log.d(TAG, "Execute action is " + action);

        this.mCallbackContext = callbackContext;

        if (action == null || !action.matches("coolMethod|getTrunk|getTrunkTest|startLocation|stopLocation|currentLocation|startSensor|stopSensor|getCurrentSensor")) {
            // TODO set message
            return false;
        } else if ("coolMethod".equals(action)) {
            String message = args.getString(0);
            this.coolMethod(message);
            return true;
        } else if ("getTrunk".equals(action)) {
            cordova.setActivityResultCallback(this);
            Intent intent = createTrunkIntent();
            if (intent.resolveActivity(this.cordova.getActivity().getPackageManager()) != null) {
                cordova.getActivity().startActivityForResult(intent, RESULT_READ_JSON);
            } else {
                this.askInstallation();
                Log.e(TAG, "Install " + Uri.parse(ESDERS_IR_URL));
            }
            return true;
        } else if ("getTrunkTest".equals(action)) {
            // Testing cordova plugin
            Intent intent = createTrunkIntent();
            if (intent.resolveActivity(this.cordova.getActivity().getPackageManager()) == null) {
                this.askInstallation();
            }
            getTrunkTest(this.getCallbackContext());
            return true;
        } else if ("startLocation".equals(action)) {
            if (!isGPSEnabled()) {
                // TODO translate
                this.mCallbackContext.error("GPS not enabled on device");
                return true;
            }

            if (!hasPermission()) {
                Log.d(TAG, "no permission -> request permission");
                // TODO deinstalleer app om te testen
                requestPermission();
            } else {
                Log.d(TAG, "permission");
                startLocation(this.getCallbackContext());
            }
            return true;
        } else if ("stopLocation".equals(action)) {
            stopLocation(this.getCallbackContext());
            return true;
        } else if ("currentLocation".equals(action)) {
            getCurrentPostion(this.getCallbackContext());
            return true;
        } else if ("getCurrentSensor".equals(action)) {
            getCurrentSensor(this.getCallbackContext());
            return true;
        } else if ("startSensor".equals(action)) {
            startSensor(this.getCallbackContext());
            return true;
        } else if ("stopSensor".equals(action)) {
            stopSensor(this.getCallbackContext());
            return true;
        }

        return false;
    }

    private Intent createTrunkIntent() {
        Intent intent = new Intent("de.esders.ir.READOUT");
        intent.putExtra(EXTRA_READ_JSON, true);
        // read only the last measurement
        intent.putExtra(EXTRA_READ_JSON_NUMBER, -1);
        return intent;
    }

    /**
     * Opens the PlayStore for the installation of Esders IR
     */
    private void askInstallation() {
        final Activity activity = this.cordova.getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String message = "The App \"Esders IR\" is not installed. Open Play Store to start the installation?";
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the corresponding page from the PlayStore
                        Uri marketUri = Uri.parse(ESDERS_IR_URL);
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        activity.startActivity(marketIntent);
                    }
                });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        Log.d(TAG,"onActivityResult called " + requestCode + " " + resultCode);

        if (RESULT_READ_JSON == requestCode) {
            // TODO check exception handling als geen verbinding met koffer via bluetooth
            Log.d(TAG, "onActivityResult data " + data);
            PluginResult result;
            if (data != null) {
                String jsonString = data.getStringExtra("json");
                Log.d(TAG, "Data " + jsonString);
                result = new PluginResult(PluginResult.Status.OK, jsonString);
            } else {
                result = new PluginResult(PluginResult.Status.ERROR, "no data");
            }
            result.setKeepCallback(true);
            this.getCallbackContext().sendPluginResult(result);
        }
    }


    private GeoLocationListener getLocationListener() {
        if (mLocationListener == null) {
            mLocationListener = new GeoLocationListener(this, TAG);
        }
        return mLocationListener;
    }

    private GeoSensorListener getSensorListener() {
        if (mSensorListener == null) {
            mSensorListener = new GeoSensorListener(this, TAG);
        }
        return mSensorListener;
    }

    private boolean isGPSEnabled() {
        Log.d(TAG, "execute isGPSEnabled");
        boolean isGPSEnabled = getLocationManager().isProviderEnabled(getProvider());
        Log.d(TAG, "GPS enabled " + isGPSEnabled);
        return isGPSEnabled;
    }

    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    public SensorManager getSensorManager() { return mSensorManager; }

    public String getProvider() {
        return mProvider;
    }

    public CallbackContext getCallbackContext() {
        return mCallbackContext;
    }

    public void win(Position position, CallbackContext callbackContext, boolean keepCallback) {
        Log.d(TAG, "win with location");
        PluginResult result = new PluginResult(PluginResult.Status.OK, converPosition(position));
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    public void win(SensorItem sensorItem, CallbackContext callbackContext, boolean keepCallback) {
        Log.d(TAG, "win with float");
        PluginResult result = new PluginResult(PluginResult.Status.OK, convertSensor(sensorItem));
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    public void win(String data, CallbackContext callbackContext) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
        result.setKeepCallback(false);
        callbackContext.sendPluginResult(result);
    }

    private void coolMethod(String message) {
        Log.d(TAG, "execute coolMethod");
        if (message != null && message.length() > 0) {
            getCallbackContext().success("Hello " + message);
        } else {
            getCallbackContext().error("Expected one non-empty string argument.");
        }
    }

    private void getTrunkTest(CallbackContext callbackContext) {
        LOG.d(TAG, "execute getTrunkTest");
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, convertTrunkTest()));
    }

    private void startLocation(CallbackContext callbackContext) {
        Log.d(TAG, "execute startLocation");
        getLocationListener().start(callbackContext);
        win(getLocationListener().getData(), callbackContext, true);
    }

    private void getCurrentSensor(CallbackContext callbackContext) {
        Log.d(TAG, "execute getCurrentSensor");
        // TODO improve (duplicatie with startSensor)
        if (mSensorListener == null) {
            getSensorListener().start(callbackContext);
        }
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, convertSensor(getSensorListener().getData())));
    }

    private void stopLocation(CallbackContext callbackContext) {
        Log.d(TAG, "execute stopLocation");
        getLocationListener().stop(callbackContext);
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }

    private void getCurrentPostion(CallbackContext callbackContext) {
        Log.d(TAG, "execute getCurrentPosition");
        win(getLocationListener().getData(), callbackContext, true);
    }

    private void startSensor(CallbackContext callbackContext) {
        Log.d(TAG, "execute startSensor");
        getSensorListener().start(callbackContext);
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }

    private void stopSensor(CallbackContext callbackContext) {
        Log.d(TAG, "execute stopSensor");
        getSensorListener().stop(callbackContext);
        // TODO make restart possible
        mSensorListener = null;
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }

    private void requestPermission() {
        cordova.requestPermission(this, GPS_POSITION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GPS_POSITION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the related task you need to do.
                    Log.d(TAG, "permission grantend");
                    startLocation(this.getCallbackContext());
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.d(TAG, "permission denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private boolean hasPermission() {
        Log.d(TAG, "execute hasPermission");
        return cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Genereer dummy data
     *
     * Kan veel mooier.
     * @return
     */
    private String convertTrunkTest() {
        Log.d(TAG, "execute convertTrunkTest");
        final String json1 = "{\"version\": 1, \"serialno\": \"27G01234\", \"swversion\": \"60020007\", \"man\": \"Esders GmbH\", ";
        final String json2 = "\"dev\": \"DT2000NL\", \"servicedate\": \"2018-07-15\", \"meas_count\": 1, \"time\": \"2014-05-20T14:16:05\", ";
        final String json3 = "\"battery\": 75, \"last_charge\": \"2017-12-21\", \"dev_info1\": [128,54500], \"dev_info2\": 200, ";
        final String json4 = "\"meas\": [{\"time_start\": \"2014-05-20T14:12:18\", \"time_end\": \"2014-05-20T14:12:18\", \"menu_no\": 2,";
        final String json5 = "\"menu_name\": \"Aansluitleidingen\", \"meas_no\": 1, \"res_blk_size\": 10, \"extra\": [\"9217XJ 40\", \"Tester 1\"],";
        final String json6 = "\"method\": 0, \"result_cnt\": 9, \"result\": {\"p_start\": [1076.2,10], \"p_end\":[1060.8,10], \"p_diff\":[1076.2,10], \"t_stabil\": [0,100], \"t_meas\":[300,100],\"meas_type\":106}}]}";

        final StringBuffer json = new StringBuffer().append(json1).append(json2).append(json3).append(json4)
                .append(json5).append(json6);

        return json.toString();
    }

    private String converPosition(Position pos) {
        Log.d(TAG, "execute convertLocation");
        JSONObject object = new JSONObject();
        try {
            object.put("timestamp", pos.getTimestamp());
            object.put("latitude", pos.getLatitude());
            object.put("longitude", pos.getLongitude());
        } catch (JSONException e) {
            // TODO exception handling
            Log.e(TAG, e.getLocalizedMessage());
            getCallbackContext().error(e.getLocalizedMessage());
        }
        return object.toString();
    }

    private String convertSensor(SensorItem sensorItem) {
        Log.d(TAG, "execute convertSensor");
        JSONObject object = new JSONObject();
        try {
            if (sensorItem != null && sensorItem.getOrientation() != null && sensorItem.getAccelerometer() != null) {
                if (sensorItem.getOrientation().getValues().length == 4) {
                    object.put("timestamp",  sensorItem.getOrientation().getTimestamp());

                    object.put("q_x",  sensorItem.getOrientation().getValues()[0]);
                    object.put("q_y",  sensorItem.getOrientation().getValues()[1]);
                    object.put("q_z",  sensorItem.getOrientation().getValues()[2]);
                    object.put("q_w",  sensorItem.getOrientation().getValues()[3]);
                }
                if (sensorItem.getAccelerometer().getValues().length == 3) {
                    object.put("acc_user_x",  sensorItem.getAccelerometer().getValues()[0]);
                    object.put("acc_user_y",  sensorItem.getAccelerometer().getValues()[1]);
                    object.put("acc_user_z",  sensorItem.getAccelerometer().getValues()[2]);
                }
            }
            if (object.length() == 0) {
                Log.d(TAG, "No sensor data available");
            }

        } catch (JSONException e) {
            // TODO exception handling
            Log.e(TAG, e.getLocalizedMessage());
        }
        return object.toString();
    }

}
