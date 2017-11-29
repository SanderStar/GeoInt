package cordova.plugin.geoint;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sander on 29-11-2017.
 */

public class GeoSensorListener implements SensorEventListener {

    private static String TAG;

    private GeoInt mOwner;

    private Sensor mSensor;

    boolean mMustReadSensor;
    private Timer mTimer = new Timer();

    private List<CallbackContext> mCallbacks = new ArrayList<CallbackContext>();

    public GeoSensorListener(GeoInt owner, String tag) {
        Log.d(TAG, "constructor GeoSensorListener");
        this.mOwner = owner;
        this.TAG = tag;
        mSensor = mOwner.getSensorManager().getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        // Vertraag collectie van sensor data
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mMustReadSensor = true;
            }
        }, 0, 1000);  // 1000 ms delay
    }

    public void start(CallbackContext callbackContext) {
        Log.d(TAG, "exectue start (sensor event listener)");
        mCallbacks.add(callbackContext);
        mOwner.getSensorManager().registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop(CallbackContext callbackContext) {
        Log.d(TAG, "exectue stop (sensor event listener)");
        mOwner.getSensorManager().unregisterListener(this);
        mOwner.win("stopped", callbackContext);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Controleer op sensor type
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            Log.d(TAG, "execute onSensorChanged");

            // Bepaal sensor data op vaste tijdstippen, anders overload aan data
            if (!mMustReadSensor) {
                return;
            }
            mMustReadSensor = false;

            StringBuffer data = new StringBuffer();
            data.append("event data ").append(sensorEvent.timestamp).append(" accuracy ").append(sensorEvent.accuracy).append(" values ");
            for (int i = 0; i < sensorEvent.values.length; i++) {
                data.append(i).append(" ").append(sensorEvent.values[i]);
            }
            Log.d(TAG, data.toString());

            for (CallbackContext callbackContext : mCallbacks) {
                mOwner.win(sensorEvent.values, callbackContext, true);
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


}
