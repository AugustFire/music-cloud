package com.young.music.cloud.shop.server.client;


import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * create_time 2019/12/26
 *
 * @author yzx
 */
@Component
@Slf4j
public class HelloFallbackFactory implements FallbackFactory<HelloFeignClient> {

    @Override
    public HelloFeignClient create(Throwable cause) {

        return name -> {
            log.error("进入回退逻辑 []", cause);
            return "hi I am fallback!";
        };
    }
}
