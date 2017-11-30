package cordova.plugin.geoint.domain;

/**
 * Created by Sander on 30-11-2017.
 */

public class Orientation {

    private float[] values;

    private long timestamp;

    public Orientation(float[] values, long timestamp) {
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
