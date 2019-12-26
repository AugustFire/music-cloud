package com.young.music.cloud.shop.server;

import com.alibaba.fastjson.JSON;
import com.young.music.cloud.shop.server.client.HelloFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源服务器
 *
 * @author yzx
 */
@SpringBootApplication
@EnableFeignClients
@RestController
@Slf4j
public class MusicCloudShopServerApplication {

    @Autowired
    private HelloFeignClient helloFeignClient;

    public static void main(String[] args) {
        SpringApplication.run(MusicCloudShopServerApplication.class, args);
    }

    @GetMapping(value = "/api")
    @PreAuthorize("hasAnyRole('USER')")
    public String success() {
        System.out.println("debug------------------------api");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return JSON.toJSONString(authentication);
    }

    @GetMapping(value = "/api2")
    public String say() {
        System.out.println("debug-----------------------api2");
        String name = "shop-server";
        return helloFeignClient.sayHello(name);
    }


}
