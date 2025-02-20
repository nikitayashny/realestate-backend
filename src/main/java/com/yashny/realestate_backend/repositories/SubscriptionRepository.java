package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByUserId(Long id);
}
