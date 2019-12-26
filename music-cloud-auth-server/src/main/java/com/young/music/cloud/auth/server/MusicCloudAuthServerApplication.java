package com.young.music.cloud.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

/**
 * 2019-8-2 认证中心
 *
 * @author yzx
 */
@SpringBootApplication
@Controller
public class MusicCloudAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicCloudAuthServerApplication.class, args);
    }

    @GetMapping("/say")
    @ResponseBody
    public String sayHello() {
        return "Hello Cloud";
    }

    @GetMapping("/user/me")
    @ResponseBody
    public Principal user(Principal user){
        return user;
    }

}
