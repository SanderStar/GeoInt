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

    public static int POSITION_UNAVAILABLE = 2;

    public GeoLocationListener(GeoInt owner, String tag) {
        this.mOwner = owner;
        this.TAG = tag;
    }

    public void start(CallbackContext callbackContext) {
        try {
            Log.d(TAG, "requesting location updates");
            mOwner.getLocationManager().requestLocationUpdates(mOwner.getProvider(), 1000, 0, this);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
            callbackContext.error(e.getLocalizedMessage());
        }
    }

    public void onLocationChanged(Location loc) {
        String data = "Location changed: timestamp "
                + new Date(loc.getTime())
                + " Lat: " + loc.getLatitude()
                + " Lng: " + loc.getLongitude();
        JSONObject object = convertLocation(loc);

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
            object.put("latitude", 52.174887299999995);
            object.put("longitude", 4.4477059);
        } catch (JSONException e) {
            // TODO exception handling
            Log.e(TAG, e.getLocalizedMessage());
        }
        return object;
    }

}
