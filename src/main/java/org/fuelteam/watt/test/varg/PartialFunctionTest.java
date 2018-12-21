package org.fuelteam.watt.test.varg;

import static org.junit.Assert.assertEquals;

import org.fuelteam.watt.lucky.varg.Partial;
import org.fuelteam.watt.lucky.varg.VArgFunction;
import org.junit.Test;

public class PartialFunctionTest {

    @Test
    public void shouldApplyVarargsFunction() {
        // given
        VArgFunction<String, String> f = Partial.vargFunction(Functions::funcVarArgs);

        // when
        VArgFunction<String, String> partial = f.arg("1").arg("2");

        // then
        assertEquals("12", partial.apply());
    }
}