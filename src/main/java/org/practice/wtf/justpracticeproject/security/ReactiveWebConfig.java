package org.practice.wtf.justpracticeproject.security;

import org.practice.wtf.justpracticeproject.security.roles.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class ReactiveWebConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.
                csrf().disable().
                authorizeExchange().
                pathMatchers("/register").permitAll().
                pathMatchers("/admin").hasRole(Role.ADMIN.name()).
                anyExchange().authenticated().
                and().
                httpBasic().
                and().
                formLogin();
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
