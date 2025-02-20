package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.dto.RequestRealtDto;
import com.yashny.realestate_backend.entities.DealType;
import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class RealtService {

    private final RealtRepository realtRepository;
    private final ImageService imageService;
    private final FavoriteRepository favoriteRepository;
    private final UserFilterRepository userFilterRepository;
    private final EmailSenderService emailSenderService;
    private final SubscriptionService subscriptionService;

    public List<Realt> getRealts(RequestRealtDto requestRealtDto) {
        int page = requestRealtDto.getPage();
        int limit = requestRealtDto.getLimit();

        List<Realt> realts = realtRepository.findAll();

        if (requestRealtDto.getDealTypeId() != 0) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getDealTypeId().equals(realt.getDealType().getId()))
                    .toList();
        }

        if (requestRealtDto.getTypeId() != 0) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getTypeId().equals(realt.getType().getId()))
                    .toList();
        }

        if (requestRealtDto.getRoomsCount() != 0 && requestRealtDto.getRoomsCount() < 5) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getRoomsCount() == realt.getRoomsCount())
                    .collect(Collectors.toList());
        }
        if (requestRealtDto.getRoomsCount() >= 5) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getRoomsCount() <= realt.getRoomsCount())
                    .collect(Collectors.toList());
        }
        if (requestRealtDto.getMaxPrice() != -1) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getMaxPrice() >= realt.getPrice())
                    .collect(Collectors.toList());
        }

        List<Realt> sortedRealts = new ArrayList<>();


        if (requestRealtDto.getSortType() == 1) {
            sortedRealts = realts.stream()
                    .sorted(Comparator.comparingLong(realt -> {
                        long score = realt.getViews() + realt.getLikes() * 5 + realt.getReposts() * 10;
                        if (subscriptionService.getSubscription(realt.getUser()) != null) {
                            score += 10000;
                        }
                        return score;
                    }))
                    .collect(Collectors.toList())
                    .reversed();
        } else if (requestRealtDto.getSortType() == 2) {
            sortedRealts = realts.stream()
                    .sorted(Comparator.comparing(Realt::getDateOfCreated))
                    .toList().reversed();
        }

        int start = Math.min(page * limit, realts.size());
        int end = Math.min(start + limit, realts.size());

        List<Realt> paginatedRealts = sortedRealts.subList(start, end);
        Page<Realt> realtPage = new PageImpl<>(paginatedRealts, PageRequest.of(page, limit), realts.size());

        for (Realt realt : realtPage.getContent()) {
            User user = realt.getUser();
            user.setPassword(null);
            realt.setUser(user);
        }
        return realtPage.getContent();
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

    public void likeRealt(Long id) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));

        realt.setLikes(realt.getLikes() + 1);
        realtRepository.save(realt);
    }

    public void viewRealt(Long id) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));

        realt.setViews(realt.getViews() + 1);
        realtRepository.save(realt);
    }

    public void repostRealt(Long id) {
        Realt realt = realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));

        realt.setReposts(realt.getReposts() + 1);
        realtRepository.save(realt);
    }

    public Realt getRealt(Long id) {
        return realtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));
    }

    public long getCount(RequestRealtDto requestRealtDto) {

        List<Realt> realts = realtRepository.findAll();

        if (requestRealtDto.getDealTypeId() != 0) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getDealTypeId().equals(realt.getDealType().getId()))
                    .toList();
        }
        if (requestRealtDto.getTypeId() != 0) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getTypeId().equals(realt.getType().getId()))
                    .toList();
        }
        if (requestRealtDto.getRoomsCount() != 0 && requestRealtDto.getRoomsCount() < 5) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getRoomsCount() == realt.getRoomsCount())
                    .collect(Collectors.toList());
        }
        if (requestRealtDto.getRoomsCount() >= 5) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getRoomsCount() <= realt.getRoomsCount())
                    .collect(Collectors.toList());
        }
        if (requestRealtDto.getMaxPrice() != -1) {
            realts = realts.stream()
                    .filter(realt -> requestRealtDto.getMaxPrice() >= realt.getPrice())
                    .collect(Collectors.toList());
        }
        return realts.size();
    }
}
