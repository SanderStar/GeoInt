package cordova.plugin.geoint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    private GeoLocationListener mLocationListener;
    private LocationManager mLocationManager;
    private CallbackContext mCallbackContext;

    private static final int GPS_POSITION  = 1;

    // Network provider for low battery usage
    private String mProvider = LocationManager.NETWORK_PROVIDER;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // TODO test logcat error printing (system shell: adb logcat -s GEO)
        Log.e(TAG, "Execute action is " + action);

        this.mCallbackContext = callbackContext;

        if (action == null || !action.matches("coolMethod|getLocation")) {
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
                getLocation();
            }
            return true;
        }


        return false;
    }

    private GeoLocationListener getListener() {
        if (mLocationListener == null) {
            mLocationListener = new GeoLocationListener(this, TAG);
        }
        return mLocationListener;
    }

    private boolean isGPSEnabled() {
        Log.d(TAG, "execute isGPSEnabled");
        boolean isGPSEnabled = getLocationManager().isProviderEnabled(getProvider());
        Log.d(TAG, "GPS enabled " + isGPSEnabled);
        return isGPSEnabled;
    }

    public  LocationManager getLocationManager() {
        return mLocationManager;
    }

    public String getProvider() {
        return mProvider;
    }

    public CallbackContext getCallbackContext() {
        return mCallbackContext;
    }

    public void win(String data, CallbackContext callbackContext, boolean keepCallback) {
        PluginResult result = new PluginResult(PluginResult.Status.OK,
                this.returnLocationJSON(loc));
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

    private void coolMethod(String message) {
        Log.d(TAG, "execute coolMethod");
        if (message != null && message.length() > 0) {
            this.mCallbackContext.success("Hello " + message);
        } else {
            this.mCallbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getLocation() {
        Log.d(TAG, "execute getLocation");
        getListener().start();
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
                    getLocation();
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


}
