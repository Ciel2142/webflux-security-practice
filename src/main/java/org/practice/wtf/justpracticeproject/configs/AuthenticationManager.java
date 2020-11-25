package org.practice.wtf.justpracticeproject.configs;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("Trying to authenticate");
        String authToken = authentication.getCredentials().toString();
        String userName;
        try {
            userName = jwtUtils.extractUsername(authToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (userName != null && jwtUtils.validateToken(authToken)) {
            log.info("passed validation");
            Claims claims = jwtUtils.getClaims(authToken);
            List<String> authorities = claims.get("authorities", List.class);
            Authentication authenticationPoint = new UsernamePasswordAuthenticationToken(
                    userName,
                    null,
                    authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
            );
            return Mono.just(authenticationPoint);
        }
        return Mono.empty();
    }
}
