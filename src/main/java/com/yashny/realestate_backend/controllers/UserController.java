package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneUser(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/realts/{id}")
    public ResponseEntity<?> usersRealts(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUsersRealts(id));
    }

}
