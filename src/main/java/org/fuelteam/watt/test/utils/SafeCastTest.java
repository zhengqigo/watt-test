package org.fuelteam.watt.test.utils;

import org.fuelteam.watt.lucky.utils.SafeCast;
import org.fuelteam.watt.lucky.utils.Vardump;

public class SafeCastTest {

    public static void main(String[] args) {
        Object val = "test";
        String result = SafeCast.cast(val).to(String.class).orElse("default");
        Vardump.print(result);
    }
}
