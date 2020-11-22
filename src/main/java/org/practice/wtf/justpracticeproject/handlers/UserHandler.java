package org.practice.wtf.justpracticeproject.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.practice.wtf.justpracticeproject.security.CustomUserDetail;
import org.practice.wtf.justpracticeproject.security.roles.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Log4j2
public class UserHandler {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid CustomUser customUser) {
        userRepository.findByUserName(customUser.getUserName()).flatMap(user -> {
            if (user != null) {
                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "User witch such username exists: " + customUser.getUserName()));
            } else {
                customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                customUser.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_" + Role.USER.name())));
                return userRepository.save(customUser);
            }
        }).subscribe(log::info);
        return new ResponseEntity<>("User: " + customUser.getUserName() + " created", HttpStatus.CREATED);
    }

    @GetMapping("/admin")
    public String getAdmin(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return "Hello admin: " + customUserDetail.getUsername();
    }

    @GetMapping("/user")
    public String getUser(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return "Hello user: " + customUserDetail.getUsername();
    }
}