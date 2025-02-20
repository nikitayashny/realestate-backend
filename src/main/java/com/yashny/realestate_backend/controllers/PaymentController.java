package com.yashny.realestate_backend.controllers;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;
import com.stripe.exception.SignatureVerificationException;
import com.yashny.realestate_backend.dto.PaymentRequest;
import com.yashny.realestate_backend.dto.StripeResponse;
import com.yashny.realestate_backend.entities.Payment;
import com.yashny.realestate_backend.entities.Subscription;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.PaymentRepository;
import com.yashny.realestate_backend.repositories.SubscriptionRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import com.yashny.realestate_backend.services.StripeService;
import com.yashny.realestate_backend.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final StripeService stripeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${stripe.session.secretKey}")
    private String endpointSecret;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody PaymentRequest paymentRequest,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            String token = authorization.substring(7);
            User user = jwtUtil.getUserFromToken(token);
            StripeResponse stripeResponse = stripeService.checkout(paymentRequest, user);
            return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid payload");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getData().getObject();

            if (!"paid".equals(session.getPaymentStatus())) {
                return ResponseEntity.status(400).body("Payment not completed");
            }

            String email = session.getMetadata().get("email");

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Payment payment = new Payment();
            payment.setUser(user);
            payment.setPaid(true);
            payment.setName(session.getMetadata().get("name"));
            payment.setQuantity(Long.parseLong(session.getMetadata().get("quantity")));
            payment.setPrice(session.getAmountTotal() / 100);

            paymentRepository.save(payment);

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setQuantity(Long.parseLong(session.getMetadata().get("quantity")));
            subscriptionRepository.save(subscription);

        }

        return ResponseEntity.ok("Webhook received");
    }

}
