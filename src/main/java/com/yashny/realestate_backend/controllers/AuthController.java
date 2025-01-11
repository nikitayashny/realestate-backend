package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.UserRepository;
import com.yashny.realestate_backend.services.CustomOAuth2UserService;
import com.yashny.realestate_backend.services.UserService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        userService.saveUser(user);
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok().body(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        var existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent() &&
                passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok().body(Map.of("token", token));
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PostMapping("/oauth2")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> token) throws OAuth2AuthenticationException {
        String googleToken = token.get("token");

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + googleToken;
        Map<String, Object> googleUserInfo = restTemplate.getForObject(url, Map.class);

        if (googleUserInfo == null) {
            throw new OAuth2AuthenticationException("Invalid Google token");
        }

        String email = (String) googleUserInfo.get("email");
        String name = (String) googleUserInfo.get("name");
        String profilePicture = (String) googleUserInfo.get("picture");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProfilePicture(profilePicture);
            newUser.setProvider("GOOGLE");
            newUser.setEnabled(true);
            userRepository.save(newUser);
        }

        String jwt = jwtUtil.generateToken(name);

        return ResponseEntity.ok().body(Map.of("token", jwt));
    }

}