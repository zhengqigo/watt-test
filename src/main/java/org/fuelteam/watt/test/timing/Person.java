package org.fuelteam.watt.test.timing;
import java.util.ArrayList;

public class Person {
    int _age;
    ArrayList<HeartRate> _heartRates;

    public Person(int age) {
        _age = age;
        _heartRates = new ArrayList<>();
    }

    public void addHeartRate(int rate, long timestamp) {
        _heartRates.add(new HeartRate(timestamp, rate));
    }

    public ArrayList<HeartRate> getHeartRates() {
        return _heartRates;
    }
}