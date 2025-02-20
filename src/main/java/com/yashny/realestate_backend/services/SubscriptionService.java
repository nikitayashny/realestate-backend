package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Subscription;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription getSubscription(User user) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(user.getId());
        return subscriptions.stream()
                .filter(subscription -> {
                    LocalDateTime endDate = subscription.getDateOfCreated().plusMonths(subscription.getQuantity());
                    return endDate.isAfter(LocalDateTime.now());
                })
                .max(Comparator.comparing(Subscription::getDateOfCreated))
                .orElse(null);
    }
}
