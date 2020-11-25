package org.practice.wtf.justpracticeproject.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String authToken) {
        return getClaims(authToken).getSubject() ;
    }

    public Claims getClaims(String authToken) {
        String key = Base64.getEncoder().encodeToString(secret.getBytes());
        return Jwts.parserBuilder().
                setSigningKey(key).
                build().
                parseClaimsJws(authToken).
                getBody();
    }

    public boolean validateToken(String authToken) {
        return getClaims(authToken).getExpiration().after(new Date());
    }

    public String generateToken(CustomUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities",
                user.getAuthorities().
                        stream().
                        map(SimpleGrantedAuthority::getAuthority).
                        collect(Collectors.toSet()));
        return Jwts.builder().
                setClaims(claims).
                setSubject(user.getUserName()).
                setIssuedAt(new Date()).
                setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(7))).
                signWith(Keys.hmacShaKeyFor(secret.getBytes())).
                compact();
    }
}
