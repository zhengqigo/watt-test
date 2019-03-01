package org.fuelteam.watt.test.money;

import java.util.List;

import org.fuelteam.watt.lucky.money.LeftPacket;
import org.fuelteam.watt.lucky.money.RedPacket;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class TestRedPacket {

    public void test() {
        
        Vardump.print(RedPacket.randomSigma(100, 5, 0.1, 50.0));

        LeftPacket leftPacket = new LeftPacket();
        leftPacket.setRemainMoney(100.0);
        leftPacket.setRemainSize(5);
        leftPacket.setMinMoney(0.1);
        int i = 0;
        List<Double> randomMoney = Lists.newArrayList();
        while (i < 5) {
            randomMoney.add(RedPacket.getRandomMoney(leftPacket, 1));
            i++;
        }
        Vardump.print(randomMoney);
    }
}
