package org.fuelteam.watt.test.varg;
import static org.junit.Assert.assertEquals;

import org.fuelteam.watt.lucky.varg.Partial;
import org.fuelteam.watt.lucky.varg.VArgConsumer;
import org.junit.Test;

public class PartialConsumersTest {

    @Test
    public void shouldApplyVarargsFunction() {
        // given
        VArgConsumer<String> f = Partial.vargConsumer(Consumers::funcVarArgs);

        // when
        f.arg("1").arg("2").apply();

        // then
        assertEquals("12", Consumers.value);
    }
}