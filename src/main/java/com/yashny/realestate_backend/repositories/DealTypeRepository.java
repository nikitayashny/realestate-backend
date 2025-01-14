package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealTypeRepository extends JpaRepository<DealType, Long> {
}
