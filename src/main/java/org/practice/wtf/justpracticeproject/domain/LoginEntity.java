package org.practice.wtf.justpracticeproject.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginEntity {
    private CustomUser givenUser;
    private CustomUser foundUser;
}
