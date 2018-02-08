package cordova.plugin.geoint;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import cordova.plugin.geoint.domain.Position;

public class GeoLocationListener implements LocationListener {

    private static String TAG;

    private GeoInt mOwner;

    private Position mPosition = new Position();

    private List<CallbackContext> mCallbacks = new ArrayList<CallbackContext>();

    public static int POSITION_UNAVAILABLE = 2;

    public GeoLocationListener(GeoInt owner, String tag) {
        this.mOwner = owner;
        this.TAG = tag;
    }

    public void start(CallbackContext callbackContext) {
        Log.d(TAG, "execute start");
        try {
            Location loc = mOwner.getLocationManager().getLastKnownLocation(mOwner.getProvider());
            convert(loc);
            mOwner.getLocationManager().requestLocationUpdates(mOwner.getProvider(), 0, 0, this);
            mCallbacks.add(callbackContext);
            Log.d(TAG, getLocation(mPosition));
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
            mOwner.getCallbackContext().error(e.getLocalizedMessage());
        }
    }

    public void stop(CallbackContext callbackContext) {
        Log.d(TAG, "execute stop");
        mOwner.getLocationManager().removeUpdates(this);
    }

    public void onLocationChanged(Location loc) {
        Log.d(TAG, "execute onLocationChanged");

        convert(loc);

        Log.d(TAG, getLocation(mPosition));
    }

    private void convert(Location loc) {
        mPosition = new Position();

        if (loc != null) {
            mPosition.setTimestamp(loc.getTime());
            mPosition.setLatitude(loc.getLatitude());
            mPosition.setLongitude(loc.getLongitude());
        } else {
            Log.d(TAG, "Unknown location (null object)");
        }
    }

    private String getLocation(Position pos) {
        return "Position changed: timestamp "
                + pos.getTimestamp()
                + " Lat: " + pos.getLatitude()
                + " Lng: " + pos.getLongitude();
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

    public Position getData() {
        return mPosition;
    }

}
