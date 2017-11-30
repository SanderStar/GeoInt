package cordova.plugin.geoint;

import android.hardware.SensorEvent;

/**
 * Created by Sander on 30-11-2017.
 */

public class SensorItem {

    private float[] values;

    private long timestamp;

    public SensorItem(SensorEvent sensorEvent) {
        this.setValues(sensorEvent.values);
        this.timestamp = sensorEvent.timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }
}
