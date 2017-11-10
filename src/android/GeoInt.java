package cordova.plugin.geoint;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.Context;

import android.location.LocationManager;
import android.location.Criteria;
import android.location.Location;

/**
 * This class echoes a string called from JavaScript.
 */
public class GeoInt extends CordovaPlugin {

    public static final String TAG = "GEO";

    private LocationManager mLocationManager;

    @Override
  	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    		super.initialize(cordova, webView);
    		mLocationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

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
      Criteria criteria = new Criteria();
      String bestProvider = mLocationManager.getBestProvider(criteria, false);
      Location location = mLocationManager.getLastKnownLocation(bestProvider);

      JSONObject position = new JSONObject();
      try {
          position.put("latitude", location.getLatitude());
          position.put("longitude", location.getLongitude());
      } catch (JSONException e) {
          // TODO exception handling
          Log.e(TAG, e.getLocalizedMessage());
      }

      callbackContext.success(position.toString());
    }
}
