package com.young.music.cloud.auth.server.conf.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 鉴权/资源 配置
 *
 * @author yzx
 * @date 2019-4-29
 * reference:
 * https://docs.spring.io/spring-security/site/docs/5.0.4.RELEASE/reference/htmlsingle/
 */
@Configuration
public class OAuth2ServerConfig {


    @Configuration
    @EnableResourceServer
    public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .formLogin()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/**")
                    .permitAll();
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        TokenStore redisTokenStore;

        @Autowired
        ClientDetailsService clientDetailsService;

        @Autowired
        UserDetailsService userDetailsService;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("yzx_client")
                    .scopes("all")
                    .secret("$2a$10$BT1oS.cFuJF/GMoh.t7bq.Sp17siiB7.qL2TlV5R/7k8uT7ZJK4XO")
                    .authorities("ROLE_CLIENT")
                    .authorizedGrantTypes("password", "refresh_token");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints
                    .tokenStore(redisTokenStore)
//                    .tokenServices(customerDefaultTokenServices())
                    .authenticationManager(authenticationManager)
                    .userDetailsService(userDetailsService)
                    .allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);

        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) {
            //允许表单认证
            security
                    //permitAll /oauth/token_key
                    .tokenKeyAccess("permitAll()")
                    //permitAll /oauth/check_token
                    .checkTokenAccess("permitAll()")
                    .allowFormAuthenticationForClients();
        }
    }
}
