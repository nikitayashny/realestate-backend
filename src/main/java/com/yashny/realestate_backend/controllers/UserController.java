package com.yashny.realestate_backend.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, String> getUserProfile(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "message", "Welcome to your profile!"
        );
    }
}
