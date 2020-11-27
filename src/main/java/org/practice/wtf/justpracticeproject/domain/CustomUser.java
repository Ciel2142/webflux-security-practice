package org.practice.wtf.justpracticeproject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser {
    @Id
    private String id;
    @NotNull
    @Email
    private String userName;
    @Size(min = 6)
    private String password;
    private List<SimpleGrantedAuthority> authorities;
}
