package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.services.UserService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneUser(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/realts/{id}")
    public ResponseEntity<?> usersRealts(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUsersRealts(id));
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getUsersChats(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            Long id = user.getId();
            return ResponseEntity.ok(userService.getUserChats(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
