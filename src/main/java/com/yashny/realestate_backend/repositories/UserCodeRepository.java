package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
    Optional<UserCode> findByEmail(String email);
}