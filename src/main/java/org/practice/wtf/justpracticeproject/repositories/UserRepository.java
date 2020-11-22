package org.practice.wtf.justpracticeproject.repositories;

import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<CustomUser, String> {
    Mono<CustomUser> findByUserName(String userName);
}
