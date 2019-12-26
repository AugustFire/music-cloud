package com.young.music.cloud.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 2019-8-2 统一网关
 *
 * @author yzx
 * @see EnableEurekaClient 注解可省略
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MusicCloudApiGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(MusicCloudApiGatewayApplication.class, args);
    }

}
