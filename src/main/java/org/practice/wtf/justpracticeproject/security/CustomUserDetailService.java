package org.practice.wtf.justpracticeproject.security;

import lombok.RequiredArgsConstructor;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        // Wasn't able to throw exception and it in postman, only 401 if user doesn't exists or wrong user
        return userRepository.findByUserName(s).
                switchIfEmpty(Mono.error(new UsernameNotFoundException(s))).
                map(CustomUserDetail::new);
    }
}
