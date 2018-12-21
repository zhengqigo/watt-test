package org.fuelteam.watt.test.varg;

public class Consumers {

    static String value;

    static void funcVarArgs(String... args) {
        final StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        value = builder.toString();
    }
}