package org.practice.wtf.justpracticeproject;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.practice.wtf.justpracticeproject.repositories.UserRepository;
import org.practice.wtf.justpracticeproject.security.roles.Role;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class JustPracticeProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(JustPracticeProjectApplication.class, args);
    }
}


@Component
@RequiredArgsConstructor
@Log4j2
class ReadyTest {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        CustomUser user = new CustomUser();
        user.setId(null);
        user.setUserName("parasite@mail.ru");
        user.setPassword(bCryptPasswordEncoder.encode("21312131"));
        user.setAuthorities(Role.ADMIN.getAuthorities());
        userRepository.deleteAll().subscribe(log::info);
        userRepository.save(user).subscribe(log::info);
        System.out.println(userRepository.findByUserName(user.getUserName()).block());
    }
}
