package cordova.plugin.geoint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    private GeoLocationListener mLocationListener;
    private GeoSensorListener mSensorListener;
    private LocationManager mLocationManager;
    private SensorManager mSensorManager;
    private CallbackContext mCallbackContext;

    private static final int GPS_POSITION  = 1;

    // Network provider for low battery usage
    private String mProvider = LocationManager.NETWORK_PROVIDER;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "execute initialize");
        super.initialize(cordova, webView);
        mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // TODO test logcat error printing (system shell: adb logcat -s GEO)
        Log.e(TAG, "Execute action is " + action);

        this.mCallbackContext = callbackContext;

        if (action == null || !action.matches("coolMethod|getLocation|stopLocation|startSensor|stopSensor")) {
            // TODO set message
            return false;
        }

        if ("coolMethod".equals(action)) {
            String message = args.getString(0);
            this.coolMethod(message);
            return true;
        }

        if (!isGPSEnabled()) {
            // TODO translate
            this.mCallbackContext.error("GPS not enabled on device");
            return true;
        }

        if ("getLocation".equals(action)) {
            if (!hasPermission()) {
                Log.d(TAG, "no permission -> request permission");
                // TODO deinstalleer app om te testen
                requestPermission();
            } else {
                Log.d(TAG, "permission");
                getLocation(this.getCallbackContext());
            }
            return true;
        }

        if ("stopLocation".equals(action)) {
            stopLocation(this.getCallbackContext());
        }

        if ("startSensor".equals(action)) {
            getSensorListener().start();
        }
        if ("stopSensor".equals(action)) {
            getSensorListener().stop();
        }

        return false;
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

    public void win(Location location, CallbackContext callbackContext, boolean keepCallback) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, convertLocation(location));
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

    private void getLocation(CallbackContext callbackContext) {
        Log.d(TAG, "execute getLocation");
        getLocationListener().start(callbackContext);
    }

    private void stopLocation(CallbackContext callbackContext) {
        Log.d(TAG, "execute stopLocation");
        getLocationListener().stop(callbackContext);
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
                    getLocation(this.getCallbackContext());
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

    private String convertLocation(Location loc) {
        Log.d(TAG, "execute convertLocation");
        JSONObject object = new JSONObject();
        try {
            object.put("timestamp", loc.getTime());
            object.put("latitude", loc.getLatitude());
            object.put("longitude", loc.getLongitude());
        } catch (JSONException e) {
            // TODO exception handling
            Log.e(TAG, e.getLocalizedMessage());
            getCallbackContext().error(e.getLocalizedMessage());
        }
        return object.toString();
    }


}
