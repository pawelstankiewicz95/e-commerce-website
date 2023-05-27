package com.pawelapps.ecommerce.configuration;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/cart/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("admin")

                        .requestMatchers(HttpMethod.POST, "/api/product-categories/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/product-categories/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.DELETE, "/api//product-categories/**").hasAuthority("admin")
                        .anyRequest().permitAll()
                )

                .oauth2ResourceServer().jwt();


        Okta.configureResourceServer401ResponseBody(httpSecurity);
        httpSecurity.setSharedObject(ContentNegotiationStrategy.class, new HeaderContentNegotiationStrategy());
        httpSecurity.csrf().disable();
        httpSecurity.cors();
        return httpSecurity.build();
    }
}
