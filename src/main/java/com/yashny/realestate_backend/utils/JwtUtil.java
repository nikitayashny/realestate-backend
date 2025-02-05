package com.yashny.realestate_backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserService userService;

    public String generateToken(String username, String email, String role, Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000 * 24);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("email", email)
                .withClaim("role", role)
                .withClaim("userName", username)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }


    public Authentication validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decoded = verifier.verify(token);
            String email = decoded.getClaim("email").asString();
            User user = userService.findByEmail(email).orElseThrow();
            return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        } catch (TokenExpiredException e) {
            return null;
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public boolean isAdmin(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT decoded = verifier.verify(token);
        String role = decoded.getClaim("role").asString();
        return role.equals("ADMIN");
    }

    public boolean isSuperAdmin(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT decoded = verifier.verify(token);
        String role = decoded.getClaim("role").asString();
        return role.equals("SUPER_ADMIN");
    }

    public User getUserFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT decoded = verifier.verify(token);
        String email = decoded.getClaim("email").asString();
        return userService.findByEmail(email).orElseThrow();
    }

}