package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
