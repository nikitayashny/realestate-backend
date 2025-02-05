package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.dto.ConfirmDto;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.entities.UserCode;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.UserCodeRepository;
import com.yashny.realestate_backend.repositories.UserFilterRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import com.yashny.realestate_backend.services.EmailSenderService;
import com.yashny.realestate_backend.services.UserCodeService;
import com.yashny.realestate_backend.services.UserService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final UserCodeService userCodeService;
    private final UserCodeRepository userCodeRepository;
    private final UserFilterRepository userFilterRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        Random random = new Random();
        String confirmationCode = String.valueOf(100000 + random.nextInt(900000));
        emailSenderService.sendConfirmationCode(user.getEmail(), confirmationCode);
        userCodeService.create(user.getEmail(), confirmationCode);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody @Validated ConfirmDto user) {
        UserCode userCode = userCodeService.findByEmail(user.getEmail());

        if (!userCode.getCode().equals(user.getCode())) {
            return ResponseEntity.badRequest().body("Неверный код подтверждения");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setRole("USER");
        userService.saveUser(newUser);

        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), newUser.getRole(), newUser.getId());

        userCodeRepository.delete(userCode);

        return ResponseEntity.ok().body(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        var existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent() &&
                passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword()) &&
                existingUser.get().isEnabled()) {
            String token = jwtUtil.generateToken(
                    existingUser.get().getUsername(), user.getEmail(), existingUser.get().getRole(), existingUser.get().getId());

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
            newUser.setRole("USER");
            userRepository.save(newUser);

            UserFilter userFilter = new UserFilter();
            userFilter.setUser(newUser);
            userFilterRepository.save(userFilter);

            String jwt = jwtUtil.generateToken(name, email, "USER", newUser.getId());
            return ResponseEntity.ok().body(Map.of("token", jwt));
        }

        if (existingUser.get().isEnabled()) {
            String jwt = jwtUtil.generateToken(existingUser.get().getUsername(), email, existingUser.get().getRole(), existingUser.get().getId());
            return ResponseEntity.ok().body(Map.of("token", jwt));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ваш аккаунт заблокирован");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> auth(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        if (authorization == null) {
            return ResponseEntity.ok().build();
        }
        String jwt = authorization.substring(7);

        return ResponseEntity.ok().body(Map.of("token", jwt));
    }

}