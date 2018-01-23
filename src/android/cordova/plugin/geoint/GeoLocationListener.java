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
            mOwner.getLocationManager().requestLocationUpdates(mOwner.getProvider(), 0, 0, this);
            mCallbacks.add(callbackContext);
            mOwner.win("started", callbackContext);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
            mOwner.getCallbackContext().error(e.getLocalizedMessage());
        }
    }

    public void stop(CallbackContext callbackContext) {
        Log.d(TAG, "execute stop");
        mOwner.getLocationManager().removeUpdates(this);
        mOwner.win("stopped", callbackContext);
    }

    public void onLocationChanged(Location loc) {
        Log.d(TAG, "execute onLocationChanged");

        String data = "Position changed: timestamp "
                + new Date(loc.getTime())
                + " Lat: " + loc.getLatitude()
                + " Lng: " + loc.getLongitude();

        Log.d(TAG, data);

        mPosition.setTimestamp(loc.getTime());
        mPosition.setLatitude(loc.getLatitude());
        mPosition.setLongitude(loc.getLongitude());

        // TODO don't send data anymore
        /*
        for (CallbackContext callbackContext : mCallbacks) {
            mOwner.win(loc, callbackContext, true);
        }
        */
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
