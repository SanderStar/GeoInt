package cordova.plugin.geoint.domain;

/**
 * Created by sande on 23-1-2018.
 */

public class Position {

    private long timestamp;

    private double latitude;

    private double longitude;

    public Position() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
