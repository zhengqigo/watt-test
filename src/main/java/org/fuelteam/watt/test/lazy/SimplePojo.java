package org.fuelteam.watt.test.lazy;

import java.util.concurrent.atomic.AtomicInteger;

public class SimplePojo {

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