package org.fuelteam.watt.test;

import org.fuelteam.watt.test.call.CallTest;
import org.fuelteam.watt.test.http.HttpTest;
import org.fuelteam.watt.test.money.TestRedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "cn.fuelteam.watt.test", "org.fuelteam" })
public class TestApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Autowired
    private CallTest callTest;

    @Autowired
    private HttpTest httptest;

    @Autowired
    private TestRedPacket testRedPacket;

    @Override
    public void run(String... args) throws Exception {
        callTest.test();
        httptest.testPostForm();
        httptest.testGetWithParams();
        httptest.testAsmx();
        testRedPacket.test();
    }
}
