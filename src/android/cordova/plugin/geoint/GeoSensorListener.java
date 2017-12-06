package cordova.plugin.geoint;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.util.ArrayList;
import java.util.List;

import cordova.plugin.geoint.domain.Accelerometer;
import cordova.plugin.geoint.domain.Orientation;
import cordova.plugin.geoint.domain.SensorItem;

/**
 * Created by Sander on 29-11-2017.
 */

public class GeoSensorListener implements SensorEventListener {

    // TODO synchronisatie echt nodig?!
    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and
     * fusion algorithm
     */
    protected final Object syncToken = new Object();

    /**
     * Accelerometer values
     */
    private float[] accelerometerValues = new float[3];

    /**
     * Rotation values
     */
    private float[] rotationValues = new float[4];

    /**
     * Sensor timestamp
     */
    private long timestamp;

    private static String TAG;

    private GeoInt mOwner;

    private static final int TYPE_ACCELEROMETER = Sensor.TYPE_LINEAR_ACCELERATION;
    private static final int TYPE_ORIENTATION = Sensor.TYPE_GAME_ROTATION_VECTOR;

    /**
     * The list of sensors used by this provider
     */
    protected List<Sensor> sensorList = new ArrayList<Sensor>();

    private List<CallbackContext> mCallbacks = new ArrayList<CallbackContext>();

    private SensorItem mSensorItem = new SensorItem();

    public GeoSensorListener(GeoInt owner, String tag) {
        Log.d(TAG, "constructor GeoSensorListener");
        this.mOwner = owner;
        this.TAG = tag;
        sensorList.add(mOwner.getSensorManager().getDefaultSensor(TYPE_ACCELEROMETER));
        sensorList.add(mOwner.getSensorManager().getDefaultSensor(TYPE_ORIENTATION));
    }

    public void start(CallbackContext callbackContext) {
        Log.d(TAG, "exectue start (sensor event listener)");
        mCallbacks.add(callbackContext);
        for (Sensor sensor : sensorList) {
            mOwner.getSensorManager().registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void stop(CallbackContext callbackContext) {
        Log.d(TAG, "exectue stop (sensor event listener)");
        for (Sensor sensor : sensorList) {
            mOwner.getSensorManager().unregisterListener(this, sensor);
        }
        // TODO return result with json
        mOwner.win("stopped", callbackContext);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Controleer op sensor type
        if (sensorEvent.sensor.getType() == TYPE_ACCELEROMETER) {
            Log.d(TAG, "execute onSensorChanged TYPE_ACCELEROMETER");

            synchronized (syncToken) {
                timestamp = sensorEvent.timestamp;
                accelerometerValues = sensorEvent.values.clone();
            }

            mSensorItem.setAccelerometer(new Accelerometer(accelerometerValues, timestamp));

            logData(sensorEvent.sensor.getName(), sensorEvent);

        }  else if (sensorEvent.sensor.getType() == TYPE_ORIENTATION) {
            Log.d(TAG, "execute onSensorChanged TYPE_GAME_ROTATION_VECTOR");

            synchronized (syncToken) {
                timestamp = sensorEvent.timestamp;
                rotationValues = sensorEvent.values.clone();
            }

            mSensorItem.setOrientation(new Orientation(rotationValues, timestamp));

            logData(sensorEvent.sensor.getName(), sensorEvent);

            for (CallbackContext callbackContext : mCallbacks) {
                mOwner.win(mSensorItem, callbackContext, true);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "execute onAccuracyChanged");
    }

    private void logData(String type, SensorEvent sensorEvent) {
        StringBuffer data = new StringBuffer();
        data.append(type).append(" event data ").append(sensorEvent.timestamp).append(" accuracy ").append(sensorEvent.accuracy).append(" values ");
        for (int i = 0; i < sensorEvent.values.length; i++) {
            data.append(i).append(" ").append(sensorEvent.values[i]);
        }
        Log.d(TAG, data.toString());
    }


    public SensorItem getData() {
        synchronized (syncToken) {
            return mSensorItem;
        }
    }
}
