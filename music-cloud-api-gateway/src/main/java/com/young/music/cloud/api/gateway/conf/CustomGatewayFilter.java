package com.young.music.cloud.api.gateway.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author yzx
 * create_time 2020/6/3
 */
@Slf4j
@Component
public class CustomGatewayFilter implements GlobalFilter, Ordered {

    private static final String ELAPSED_TIME_BEGIN = "elapsedTimeBegin";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(ELAPSED_TIME_BEGIN, System.currentTimeMillis());
        return chain.filter(exchange).then(
                Mono.fromRunnable(()->{
                    Long startTime = (Long) exchange.getAttribute(ELAPSED_TIME_BEGIN);
                    if (startTime != null) {
                        log.info(exchange.getRequest().getURI().getRawPath() + ": " + (System.currentTimeMillis() - startTime) + "ms");
                    }

                })

        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
