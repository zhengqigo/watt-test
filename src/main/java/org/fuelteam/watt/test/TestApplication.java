package org.fuelteam.watt.test;

import java.io.IOException;
import java.util.List;

import org.fuelteam.watt.lucky.utils.OSUtil;
import org.fuelteam.watt.lucky.utils.RedissonUtil;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.fuelteam.watt.test.call.CallTest;
import org.fuelteam.watt.test.http.HttpTest;
import org.fuelteam.watt.test.lazy.SimplePojo;
import org.fuelteam.watt.test.money.TestRedPacket;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;

@SpringBootApplication(scanBasePackages = { "cn.fuelteam.watt.test", "org.fuelteam" })
public class TestApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Value("${redisMode:cluster}")
    private String redisMode;

    @Bean
    public RedissonClient redissonClient() throws IOException {
        String ymlFile = "redisson-" + (redisMode.equalsIgnoreCase("single") ? "single" : "cluster") + ".yml";
        Config config = Config.fromYAML(new ClassPathResource(ymlFile).getInputStream());
        TransportMode transportMode = OSUtil.unix() ? TransportMode.EPOLL : TransportMode.NIO;
        config.setTransportMode(transportMode);
        return Redisson.create(config);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        RedissonClient redissonClient = ctx.getBean(RedissonClient.class);
        CallTest callTest = ctx.getBean(CallTest.class);
        HttpTest httptest = ctx.getBean(HttpTest.class);
        TestRedPacket testRedPacket = ctx.getBean(TestRedPacket.class);

        return args -> {
            callTest.test();
            httptest.testPostForm();
            httptest.testGetWithParams();
            httptest.testAsmx();
            testRedPacket.test();

            long x = System.currentTimeMillis();
            for (long i = 0; i < 10000l; i++) {
                redissonClient.getBucket("test" + i).setAsync(new SimplePojo("test" + i));
            }
            Vardump.print(RedissonUtil.getAsync(redissonClient, "test0", SimplePojo.class).getValue());

            redissonClient.getBucket("list").set(Lists.newArrayList(new SimplePojo("0"), new SimplePojo("1")));

            @SuppressWarnings("unchecked")
            List<SimplePojo> list = (List<SimplePojo>) redissonClient.getBucket("list").get();
            for (SimplePojo pojo : list) {
                Vardump.print(pojo.getValue());
            }
            long y = (System.currentTimeMillis() - x);
            Vardump.print(y);
        };
    }
}
