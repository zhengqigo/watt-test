package org.fuelteam.watt.test.call;

import org.fuelteam.watt.lucky.async.AbstractFunction;
import org.fuelteam.watt.lucky.async.Async;
import org.fuelteam.watt.lucky.async.Callback;
import org.fuelteam.watt.lucky.print.Vardump;
import org.springframework.stereotype.Component;

@Component
public class CallTest {

    public void test() {
        Callback<Integer> cb = new Callback<Integer>() {
            @Override
            public void onSuccess(Integer t) {
                Vardump.print("onSuccess");
            }

            @Override
            public void onFailure(Exception ex) {
                Vardump.print("onFailure");
            }
        };
        Async.doAsync(new AbstractFunction<Integer>(cb) {
            @Override
            public Integer execute() throws Exception {
                return 1;
            }
        });
    }
}