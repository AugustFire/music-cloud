package com.young.music.cloud.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 2019-8-2 统一网关
 *
 * @author yzx
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableEurekaClient
public class MusicCloudApiGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(MusicCloudApiGatewayApplication.class, args);
    }

}
