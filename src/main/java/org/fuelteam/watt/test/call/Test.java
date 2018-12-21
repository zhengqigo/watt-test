package org.fuelteam.watt.test.call;

import org.fuelteam.watt.lucky.call.Async;
import org.fuelteam.watt.lucky.call.AbstractFunction;
import org.fuelteam.watt.lucky.call.Callback;
import org.fuelteam.watt.lucky.utils.Vardump;

public class Test {

    public static void main(String[] args) throws Exception {
        Callback<Integer> cb = new Callback<Integer>() {
            @Override
            public void onSuccess(Integer t) {
                Vardump.print("succeded");
            }

            @Override
            public void onFailure(Exception ex) {
                Vardump.print("failed");
            }
        };

        Async async = new Async(); // ApplicationContextUtil.getBean(Async.class)
        async.doAsync(new AbstractFunction<Integer>(cb) {
            @Override
            public Integer execute() throws Exception {
                return 1;
            }
        });
    }
}
