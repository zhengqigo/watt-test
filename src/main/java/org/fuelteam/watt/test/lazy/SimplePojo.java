package org.fuelteam.watt.test.lazy;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class SimplePojo implements Serializable{

    private static final long serialVersionUID = -483045036435445656L;

    private static final AtomicInteger INSTANCES = new AtomicInteger(0);

    private final String value;

    public SimplePojo(String value) {
        this.value = value;
        INSTANCES.incrementAndGet();
    }

    public String getValue() {
        return value;
    }

    public static int getInstanceCount() {
        return INSTANCES.get();
    }

    public static void resetInstanceCount() {
        INSTANCES.set(0);
    }
}