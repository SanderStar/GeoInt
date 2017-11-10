package cordova.plugin.geoint;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import android.location.LocationManager;
import android.location.Criteria;
import android.location.Location;

/**
 * This class echoes a string called from JavaScript.
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // TODO test logcat error printing (system shell: adb logcat -s GEO)
        Log.e(TAG, "Test action is " + action);
        if ("coolMethod".equals(action)) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if ("getLocation".equals(action)) {
          this.getLocation(callbackContext);
          return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success("Hello " + message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void getLocation(CallbackContext callbackContext) {
      LocationManager locationManager = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
      Criteria criteria = new Criteria();
      String bestProvider = locationManager.getBestProvider(criteria, false);
      Location location = locationManager.getLastKnownLocation(bestProvider);

      JSONObject position = new JSONObject();
      position.put("latitude", location.getLatitude());
      position.put("longitude", location.getLongitude());

      callbackContext.success(position.toString());
    }
}
