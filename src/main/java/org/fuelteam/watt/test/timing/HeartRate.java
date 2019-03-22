package org.fuelteam.watt.test.timing;
public class HeartRate {
    long _timestamp;
    int _heartRate;

    public HeartRate(long timestamp, int heartRate) {
        _timestamp = timestamp;
        _heartRate = heartRate;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public int getHeartRate() {
        return _heartRate;
    }
}