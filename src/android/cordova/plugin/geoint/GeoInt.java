package cordova.plugin.geoint;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    private GeoLocationListener mLocationListener;
    private LocationManager mLocationManager;


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

        if (action == null || !action.matches("coolMethod|getLocation")) {
            // TODO set message
            return false;
        }

        if ("coolMethod".equals(action)) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }

        if (!isGPSEnabled()) {
            // TODO translate
            callbackContext.error("GPS not enabled on device");
            return true;
        }

        if ("getLocation".equals(action)) {
            this.getLocation(callbackContext);
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

    private void coolMethod(String message, CallbackContext callbackContext) {
        Log.d(TAG, "execute coolMethod");
        if (message != null && message.length() > 0) {
            callbackContext.success("Hello " + message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getLocation(CallbackContext callbackContext) {
        Log.d(TAG, "execute getLocation");
        getListener().start(callbackContext);
    }


}
