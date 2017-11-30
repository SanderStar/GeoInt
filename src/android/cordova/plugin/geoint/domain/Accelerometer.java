package cordova.plugin.geoint.domain;

/**
 * Created by Sander on 30-11-2017.
 */

public class Accelerometer {

    private float[] values;

    private long timestamp;

    public Accelerometer(float[] values, long timestamp) {
        this.values = values;
        this.timestamp = timestamp;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
