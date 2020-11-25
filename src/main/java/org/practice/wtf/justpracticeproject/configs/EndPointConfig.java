package org.practice.wtf.justpracticeproject.configs;

import lombok.extern.log4j.Log4j2;
import org.practice.wtf.justpracticeproject.handlers.RegistrationAndAuthenticationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Log4j2
class EndPointConfig {

    @Bean
    RouterFunction<?> routeRegistration(RegistrationAndAuthenticationHandler handle) {
        return route().
                POST("/register", handle::handleUserRegistration).
                POST("/login", handle::login).
                GET("/myName", handle::getUserName).
                GET("/", handle::getUser).
                build();
    }
}
