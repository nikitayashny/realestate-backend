package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.FavoriteRepository;
import com.yashny.realestate_backend.repositories.RealtRepository;
import com.yashny.realestate_backend.repositories.UserFilterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RealtService {

    private final RealtRepository realtRepository;
    private final ImageService imageService;
    private final FavoriteRepository favoriteRepository;
    private final UserFilterRepository userFilterRepository;
    private final EmailSenderService emailSenderService;

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

        List<UserFilter> userFilterList = userFilterRepository.findAll();
        List<UserFilter> activeUserFilters = userFilterList.stream()
                .filter(UserFilter::isActive)
                .toList();

        for (UserFilter filter : activeUserFilters) {
            boolean matches = true;

            if (filter.getCity() != null && !filter.getCity().isEmpty() && !filter.getCity().equals(realt.getCity())) {
                matches = false;
            }

            if (filter.getMaxPrice() != 0 && filter.getMaxPrice() < realt.getPrice()) {
                matches = false;
            }

            if (filter.getRoomsCount() != 0 && filter.getRoomsCount() != realt.getRoomsCount()) {
                matches = false;
            }

            if (filter.getType() != null && filter.getType() != realt.getType()) {
                matches = false;
            }

            if (filter.getDealType() != null && filter.getDealType() != realt.getDealType()) {
                matches = false;
            }

            if (matches) {
                String subject = "Подходящее для вас объявление!";
                String body = String.format(
                        "Здравствуйте, %s!\n\n" +
                                "Мы рады сообщить, что найдено новое объявление, подходящее под ваши фильтры:\n" +
                                "Название: %s\n" +
                                "Цена: %d\n" +
                                "Количество комнат: %d\n" +
                                "Город: %s\n" +
                                "Посмотреть можно по ссылке: %s%d\n\n" +
                                "С уважением,\n" +
                                "HomeHub.",
                        filter.getUser().getUsername(),
                        realt.getName(),
                        realt.getPrice(),
                        realt.getRoomsCount(),
                        realt.getCity(),
                        "https://localhost:3000/realt/", realt.getId()
                );
                emailSenderService.sendEmail(filter.getUser().getEmail(), subject, body);
            }
        }
    }

    @Transactional
    public boolean deleteRealt(Long id, User user) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));

        if (realt.getUser() == user || Objects.equals(user.getRole(), "ADMIN") || Objects.equals(user.getRole(), "SUPER_ADMIN")) {
            favoriteRepository.deleteAllByRealtId(id);
            realtRepository.delete(realt);
            return true;
        }

        return false;
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
