package com.yashny.realestate_backend.controllers;

import com.yashny.realestate_backend.entities.Subscription;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.services.SubscriptionService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    @GetMapping("")
    public ResponseEntity<?> checkSubscription(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            Subscription subscription = subscriptionService.getSubscription(user);
            return ResponseEntity.status(HttpStatus.OK).body(subscription);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
