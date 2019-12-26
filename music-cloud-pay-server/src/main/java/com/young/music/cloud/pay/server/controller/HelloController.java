package com.young.music.cloud.pay.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口资源
 * create_time 2019/12/26
 *
 * @author yzx
 */
@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {

    /**
     * 测试接口
     *
     * @param name 姓名
     * @return s
     */
    @GetMapping("/say")
    public String sayHello(String name) {
        String prefix = "hi! ";
        String suffix = " I am pay-server!";
        return prefix + name + suffix;
    }

}
