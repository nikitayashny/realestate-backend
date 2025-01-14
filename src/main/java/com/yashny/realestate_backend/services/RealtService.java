package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.dto.RealtDto;
import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.repositories.RealtRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RealtService {

    private final RealtRepository realtRepository;
    private final ImageService imageService;

    public List<Realt> getRealts() {
        return realtRepository.findAll();
    }

    public void addRealt(RealtDto realtDto) {
        Realt realt = new Realt();
        realt.setName(realtDto.getName());
        realt.setImages(realtDto.getImages());
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
