package com.xuecheng.content.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author Mr.M
 * @version 1.0
 * @description 资源服务配置
 * @date 2022/10/18 16:33
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {


    /**
    * @description 资源服务标识
    * @author LuoXuanwei
    * @date 2024/3/19
    */
    public static final String RESOURCE_ID = "xuecheng-plus";

    @Autowired
    TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        //资源 id
        resources.resourceId(RESOURCE_ID)
                .tokenStore(tokenStore)
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                //所有/r/**的请求必须认证通过
                //.antMatchers("/r/**", "/course/**").authenticated()
                .anyRequest().permitAll()
        ;
    }

}
