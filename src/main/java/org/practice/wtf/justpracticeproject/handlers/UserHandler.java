package org.practice.wtf.justpracticeproject.handlers;

import ch.qos.logback.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.practice.wtf.justpracticeproject.security.CustomUserDetail;
import org.practice.wtf.justpracticeproject.security.roles.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@RequiredArgsConstructor
@RestController
@Log4j2
public class UserHandler {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

//    @PostMapping("/register")
//    public Mono<ResponseEntity<?>> registerUser(@RequestBody @Valid CustomUser customUser) {
//        customUser.setAuthorities(Role.USER.getAuthorities());
//        customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
//        var saved = userRepository.save(customUser);
//        return userRepository.
//                findByUserName(customUser.getUserName()).
//                doOnSuccess(user -> {
//                    if (user != null) {
//                        throw new ResponseStatusException(HttpStatus.CONFLICT);
//                    }
//                }).
//                then(saved).
//                thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
//
//    }

    @GetMapping("/admin")
    public Mono<String> getAdmin(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return Mono.just("Hello admin: " + customUserDetail.getUsername());
    }

    @GetMapping("/user")
    public Mono<String> getUser(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return Mono.just("Hello user: " + userRepository.findByUserName(customUserDetail.getUsername()));
    }
}


@Configuration
@Log4j2
class EndPointConfig {

    @Bean
    RouterFunction<?> routeRegistration(HandleReq handle) {
        return route().
                POST("/register", handle::handleUserRegistration).
                GET("/myName", handle::getUserName).
                build();
    }
}

@Component
@Log4j2
@RequiredArgsConstructor
class HandleReq {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Mono<ServerResponse> handleUserRegistration(ServerRequest request) {
        return request.
                bodyToMono(CustomUser.class).
                map(user -> {
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    user.setAuthorities(Role.USER.getAuthorities());
                    return user;
                }).
                doOnNext(userRepository::save).
                doOnNext(log::info).
                then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getUserName(ServerRequest request) {
        return ReactiveSecurityContextHolder.
                getContext().
                map(securityContext -> securityContext.getAuthentication().getName()).
                flatMap(user -> {
                    String name = user;
                    if (user == null) {
                        name = "Stranger";
                    }
                    return ServerResponse.ok().body(Mono.just(name), String.class);
                });
    }
}
