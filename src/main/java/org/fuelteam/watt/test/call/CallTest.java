package org.fuelteam.watt.test.call;

import org.fuelteam.watt.lucky.call.AbstractFunction;
import org.fuelteam.watt.lucky.call.Async;
import org.fuelteam.watt.lucky.call.Callback;
import org.fuelteam.watt.lucky.context.ApplicationContextUtil;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.springframework.stereotype.Component;

@Component
public class CallTest {

    public void test() {
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

        Async async = ApplicationContextUtil.getBean(Async.class);
        async.doAsync(new AbstractFunction<Integer>(cb) {
            @Override
            public Integer execute() throws Exception {
                return 1;
            }
        });
    }
}
