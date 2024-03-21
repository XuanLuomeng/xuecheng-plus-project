package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 授权服务器配置
 * @date 2022/9/26 22:25
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource(name = "authorizationServerTokenServicesCustom")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * @description 客户端详情服务
     * @Param ClientDetailsServiceConfigurer
     * @author LuoXuanwei
     * @date 2024/3/21
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        // 使用in-memory存储
        clients.inMemory()
                // client_id
                .withClient("XcWebApp")
                //.secret("XcWebApp")//客户端密钥
                //客户端密钥
                .secret(new BCryptPasswordEncoder().encode("XcWebApp"))
                //资源列表
                .resourceIds("xuecheng-plus")
                // 该client允许的授权类型authorization_code,password,refresh_token,implicit,client_credentials
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
                // 允许的授权范围
                .scopes("all")
                //false跳转到授权页面
                .autoApprove(false)
                //客户端接收授权码的重定向地址
                .redirectUris("http://www.51xuecheng.cn");
    }


    /**
     * @description 令牌端点的访问配置
     * @Param AuthorizationServerEndpointsConfigurer
     * @author LuoXuanwei
     * @date 2024/3/21
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                //认证管理器
                .authenticationManager(authenticationManager)
                //令牌管理服务
                .tokenServices(authorizationServerTokenServices)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * @description 令牌端点的安全配置
     * @Param AuthorizationServerSecurityConfigurer
     * @author LuoXuanwei
     * @date 2024/3/21
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                //oauth/token_key是公开
                .tokenKeyAccess("permitAll()")
                //oauth/check_token公开
                .checkTokenAccess("permitAll()")
                //表单认证（申请令牌）
                .allowFormAuthenticationForClients();
    }
}
