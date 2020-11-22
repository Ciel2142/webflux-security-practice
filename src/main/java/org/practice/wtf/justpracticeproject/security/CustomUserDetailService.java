package org.practice.wtf.justpracticeproject.security;

import lombok.RequiredArgsConstructor;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return userRepository.findByUserName(s).
                switchIfEmpty(Mono.error(new UsernameNotFoundException("Username: " + s + " not found"))).
                map(CustomUserDetail::new);
    }
}
