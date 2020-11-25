package org.practice.wtf.justpracticeproject.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.practice.wtf.justpracticeproject.configs.JwtUtils;
import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.practice.wtf.justpracticeproject.security.roles.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

@Log4j2
@RequiredArgsConstructor
@Component
public class RegistrationAndAuthenticationHandler {

    public static final Mono<ServerResponse> SERVER_RESPONSE_MONO = ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

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

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(CustomUser.class).flatMap(user -> userRepository.findByUserName(user.getUserName()).
                        flatMap(userDb -> {
                            if (bCryptPasswordEncoder.matches(user.getPassword(), userDb.getPassword())) {
                                return ServerResponse.ok().body(fromPublisher(Mono.just("Bearer " + jwtUtils.generateToken(userDb)), String.class));
                            } else {
                                return SERVER_RESPONSE_MONO;
                            }
                        })).switchIfEmpty(SERVER_RESPONSE_MONO);
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
    
    public Mono<ServerResponse> getUser(ServerRequest request) {
        final String userName = request.queryParam("userName").orElse("parasite@mail.ru");
        return userRepository.
                findByUserName(userName).
                switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found: " + userName))).
                flatMap(user -> ServerResponse.ok().
                        contentType(MediaType.APPLICATION_JSON).
                        body(Mono.just(user), CustomUser.class));
    }
}
