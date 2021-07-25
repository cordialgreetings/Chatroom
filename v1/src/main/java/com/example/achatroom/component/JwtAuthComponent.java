package com.example.achatroom.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthComponent {

    public static final String HEADER_PREFIX = "Bearer ";
    private static final String SECRET_KEY = "Hn2ynwwDjRYr0Wgq5FigDKMtx4Sv+MjwmhA4C/c2I/E";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private final JwtBuilder builder = Jwts.builder();
    private final JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();

    public Mono<String> getToken(Mono<String> username) {
        return username.map(s -> builder.setSubject(s).signWith(key).compact());
    }

    public String validate(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith(HEADER_PREFIX)) {
            return null;
        }
        try{
            return parser.parseClaimsJws(bearerToken.substring(7)).getBody().getSubject();
        }catch (Exception e){
            return null;
        }
    }

}
