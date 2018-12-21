package org.fuelteam.watt.test.varg;

public interface Functions {

    static String funcVarArgs(String... args) {
        final StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }
}