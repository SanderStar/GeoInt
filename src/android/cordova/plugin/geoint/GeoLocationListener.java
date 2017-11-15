package cordova.plugin.geoint;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class GeoLocationListener implements LocationListener {

    private static String TAG;

    private GeoInt mOwner;

    private List<CallbackContext> mCallbacks = new ArrayList<CallbackContext>();

    public static int POSITION_UNAVAILABLE = 2;

    public GeoLocationListener(GeoInt owner, String tag) {
        this.mOwner = owner;
        this.TAG = tag;
    }

    public void start(CallbackContext callbackContext) {
        try {
            Log.d(TAG, "requesting location updates");
            mOwner.getLocationManager().requestLocationUpdates(mOwner.getProvider(), 1000, 0, this);
            mCallbacks.add(callbackContext);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
            mOwner.getCallbackContext().error(e.getLocalizedMessage());
        }
    }

    public void onLocationChanged(Location loc) {
        Log.d(TAG, "execute onLocationChanged");

        String data = "Location changed: timestamp "
                + new Date(loc.getTime())
                + " Lat: " + loc.getLatitude()
                + " Lng: " + loc.getLongitude();
        JSONObject object = convertLocation(loc);

        Log.d(TAG, "JSON " + object.toString());

        for (CallbackContext callbackContext : mCallbacks) {
            mOwner.win(object.toString(), callbackContext, false);
        }

        Log.d(TAG, data);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "provider enabled");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "status changed");
    }

    private JSONObject convertLocation(Location loc) {
        Log.d(TAG, "execute convertLocation");
        JSONObject object = new JSONObject();
        try {
            object.put("timestamp", loc.getTime());
            object.put("latitude", loc.getLatitude());
            object.put("longitude", loc.getLongitude());
        } catch (JSONException e) {
            // TODO exception handling
            Log.e(TAG, e.getLocalizedMessage());
            mOwner.getCallbackContext().error(e.getLocalizedMessage());
        }
        return object;
    }

}
