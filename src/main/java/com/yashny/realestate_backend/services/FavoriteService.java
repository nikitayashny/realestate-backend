package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Favorite;
import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.FavoriteRepository;
import com.yashny.realestate_backend.repositories.RealtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final RealtRepository realtRepository;
    private final FavoriteRepository favoriteRepository;

    public void addFavorite(Long id, User user) {
        Favorite favorite = new Favorite();
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Realt not found"));
        favorite.setUser(user);
        favorite.setRealt(realt);
        favoriteRepository.save(favorite);
    }

    public List<Realt> getFavorites(Long userId) {
        List<Favorite> listFavorites = favoriteRepository.findAllByUserId(userId);
        List<Realt> realts = new ArrayList<>();

        for (Favorite favorite : listFavorites) {
            Realt realt = realtRepository.findById(favorite.getRealt().getId())
                    .orElseThrow(() -> new RuntimeException("Realt not found"));
            realts.add(realt);
        }

        return realts;
    }

    @Transactional
    public void deleteFavorite(Long id, User user) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Realt not found"));
        favoriteRepository.deleteByUserAndRealt(user, realt);
    }
}
