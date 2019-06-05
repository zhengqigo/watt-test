package org.fuelteam.watt.test.utils;

import org.fuelteam.watt.lucky.print.Vardump;
import org.fuelteam.watt.lucky.utils.Condition;

public class ConditionTest {

    public static void main(String[] args) {
        int i = 15;
        int result = Condition.when(i % 2 == 0).then(i / 2).orElse(i * 3);
        Vardump.print(result);
    }
}
