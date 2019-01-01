package com.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * @author: Gu danpeng
 * @date: 2018-12-31
 * @version：1.0
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//        @Autowired
//        private UserApprovalHandler userApprovalHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(this.dataSource);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // @formatter:off
        clients.inMemory()
                .withClient("client1")
                .authorizedGrantTypes("authorization_code", "refresh_token", "password")
                .authorities("ROLE_CLIENT")
                .scopes("read", "write")
                .secret(passwordEncoder.encode("123456"))
                .redirectUris("http://localhost:8081/client1/login","http://127.0.0.1:8081","http://127.0.0.1:8081/client1/login")
                .autoApprove(true)
                .and()
                .withClient("client2")
                .authorizedGrantTypes("authorization_code", "refresh_token", "password")
                .authorities("ROLE_CLIENT")
                .scopes("read", "write")
                .secret(passwordEncoder.encode("123456"))
                .autoApprove(true)
                .redirectUris("http://localhost:8082/client2/login","http://127.0.0.1:8082/client2/login");
        // @formatter:on
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())//.userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //oauthServer.realm("sparklr2/client");
//            oauthServer.checkTokenAccess()
        security.tokenKeyAccess("permitAll()");
        security.checkTokenAccess("isAuthenticated()");
    }

}