package com.young.music.cloud.shop.server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * feign 测试
 * create_time 2019/12/26
 *
 * @author yzx
 */
@FeignClient(name = "music-cloud-pay-server",fallbackFactory = HelloFallbackFactory.class)
public interface HelloFeignClient {


    /**
     * feign
     *
     * @param name name
     * @return s
     */
    @GetMapping("/hello/say")
    String sayHello(@RequestParam(value = "name") String name);

}
