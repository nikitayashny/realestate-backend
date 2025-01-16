package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.Favorite;
import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUserId(Long userId);
    void deleteAllByRealtId(Long realtId);
    void deleteByUserAndRealt(User user, Realt realt);
}
