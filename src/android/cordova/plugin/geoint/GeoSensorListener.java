package cordova.plugin.geoint;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cordova.plugin.geoint.domain.Accelerometer;
import cordova.plugin.geoint.domain.Orientation;
import cordova.plugin.geoint.domain.SensorItem;

/**
 * Created by Sander on 29-11-2017.
 */

public class GeoSensorListener implements SensorEventListener {

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

    /**
     * The list of sensors used by this provider
     */
    protected List<Sensor> sensorList = new ArrayList<Sensor>();

    boolean mMustReadSensor;
    private Timer mTimer = new Timer();

    private List<CallbackContext> mCallbacks = new ArrayList<CallbackContext>();

    private SensorItem mSensorItem = new SensorItem();

    public GeoSensorListener(GeoInt owner, String tag) {
        Log.d(TAG, "constructor GeoSensorListener");
        this.mOwner = owner;
        this.TAG = tag;
        sensorList.add(mOwner.getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorList.add(mOwner.getSensorManager().getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));
        // Vertraag collectie van sensor data
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mMustReadSensor = true;
            }
        }, 0, 10);  // 1000 ms delay
    }

    public void start(CallbackContext callbackContext) {
        Log.d(TAG, "exectue start (sensor event listener)");
        mCallbacks.add(callbackContext);
        for (Sensor sensor : sensorList) {
            mOwner.getSensorManager().registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        // TODO Bepaal sensor data op vaste tijdstippen, anders overload aan data. Ook sturing vanuit ui mogelijk.
        if (!mMustReadSensor) {
            return;
        }
        mMustReadSensor = false;

        // Controleer op sensor type
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Log.d(TAG, "execute onSensorChanged TYPE_ACCELEROMETER");

            timestamp = sensorEvent.timestamp;
            accelerometerValues = sensorEvent.values.clone();

            mSensorItem.setAccelerometer(new Accelerometer(accelerometerValues, timestamp));

        }  else if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            Log.d(TAG, "execute onSensorChanged TYPE_GAME_ROTATION_VECTOR");

            timestamp = sensorEvent.timestamp;
            rotationValues = sensorEvent.values.clone();

            mSensorItem.setOrientation(new Orientation(rotationValues, timestamp));

            StringBuffer data = new StringBuffer();
            data.append("event data ").append(sensorEvent.timestamp).append(" accuracy ").append(sensorEvent.accuracy).append(" values ");
            for (int i = 0; i < sensorEvent.values.length; i++) {
                data.append(i).append(" ").append(sensorEvent.values[i]);
            }
            Log.d(TAG, data.toString());

            for (CallbackContext callbackContext : mCallbacks) {
                mOwner.win(mSensorItem, callbackContext, true);
            }

            // @see https://stackoverflow.com/questions/38951860/how-to-use-the-numbers-from-game-rotation-vector-in-android
            final float rotation[] = new float[9];
            data = new StringBuffer();
            SensorManager.getRotationMatrixFromVector(rotation, sensorEvent.values);
            data.append("rotation elements ");
            for (int i = 0; i < rotation.length; i++) {
                data.append(i).append(" ").append(rotation[i]);
            }
            Log.d(TAG, data.toString());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "execute onAccuracyChanged");
    }


    public SensorItem getData() {
        synchronized (syncToken) {
            return mSensorItem;
        }
    }
}
