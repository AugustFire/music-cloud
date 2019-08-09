package com.young.music.cloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 注册中心 2019-8-2
 *
 * @author yzx
 */
@SpringBootApplication
@EnableEurekaServer
public class MusicCloudEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicCloudEurekaServerApplication.class, args);
    }

}
