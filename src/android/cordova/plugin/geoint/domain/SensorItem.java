package cordova.plugin.geoint.domain;

/**
 * Created by Sander on 30-11-2017.
 */

public class SensorItem {

    private Accelerometer accelerometer;

    private Orientation orientation;

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
