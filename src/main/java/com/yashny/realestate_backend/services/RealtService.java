package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.DealTypeRepository;
import com.yashny.realestate_backend.repositories.RealtRepository;
import com.yashny.realestate_backend.repositories.TypeRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealtService {

    private final RealtRepository realtRepository;
    private final ImageService imageService;

    public List<Realt> getRealts() {
        List<Realt> realts = realtRepository.findAll();
        for (Realt realt : realts) {
            User user = realt.getUser();
            user.setPassword(null);
            realt.setUser(user);
        }
        return realts;
    }

    public void addRealt(Realt realt, List<String> imageUrls) {
        realt.setLikes(0L);
        realt.setReposts(0L);
        realt.setViews(0L);
        realt.setImages(imageUrls);
        realtRepository.save(realt);
    }

    public void deleteRealt(Long id) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));

        realtRepository.delete(realt);
    }

    public List<String> uploadImages(MultipartFile[] files) throws Exception {
        List<InputStream> imageStreams = new ArrayList<>();
        List<String> imageNames = new ArrayList<>();

        for (MultipartFile file : files) {
            imageStreams.add(file.getInputStream());
            imageNames.add(file.getOriginalFilename());
        }

        return imageService.uploadImages(imageStreams, imageNames);
    }
}
