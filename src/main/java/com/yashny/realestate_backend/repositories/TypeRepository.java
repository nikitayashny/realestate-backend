package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<Type, Long> {
}
