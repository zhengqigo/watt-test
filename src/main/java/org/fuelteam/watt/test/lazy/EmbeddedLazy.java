package org.fuelteam.watt.test.lazy;

import java.util.concurrent.atomic.AtomicInteger;

import org.fuelteam.watt.lucky.lazy.Lazy;

public class EmbeddedLazy<T> {

    private static final AtomicInteger INSTANCES = new AtomicInteger(0);

    private final Lazy<T> value;

    public EmbeddedLazy(Lazy<T> value) {
        this.value = value;
        INSTANCES.incrementAndGet();
    }

    public Lazy<T> getValue() {
        return value;
    }

    public static int getInstanceCount() {
        return INSTANCES.get();
    }

    public static void resetInstanceCount() {
        INSTANCES.set(0);
    }
}