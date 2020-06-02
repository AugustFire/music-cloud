package com.young.music.cloud.pay.server.filter;


import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * simple rate limiter to protect api
 * create_time 2020/1/7
 *
 * @author yzx
 */
@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    /**
     * 单机限流
     */
    private RateLimiter rateLimiter = RateLimiter.create(1);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (rateLimiter.tryAcquire()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("too many request!");
            response.getWriter().flush();
        }
    }
}
