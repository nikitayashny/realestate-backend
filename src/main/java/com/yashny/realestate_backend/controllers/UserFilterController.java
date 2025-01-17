package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.dto.UserFilterDto;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.services.UserFilterService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userfilters")
public class UserFilterController {

    private final UserFilterService userFilterService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
    public ResponseEntity<?> getUserFilter(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);

            return ResponseEntity.ok(userFilterService.getUserFilter(user.getId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<?> setUserFilter(@ModelAttribute UserFilterDto userFilterDto,
                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            userFilterService.setUserFilter(userFilterDto, user.getId());

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
