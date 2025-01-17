package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.dto.UserFilterDto;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.DealTypeRepository;
import com.yashny.realestate_backend.repositories.TypeRepository;
import com.yashny.realestate_backend.repositories.UserFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserFilterService {

    private final UserFilterRepository userFilterRepository;
    private final TypeRepository typeRepository;
    private final DealTypeRepository dealTypeRepository;

    public UserFilter getUserFilter(Long id) {
        return userFilterRepository.findByUserId(id);
    }

    public void setUserFilter(UserFilterDto userFilterDto, Long userId) {
        UserFilter userFilter = userFilterRepository.findByUserId(userId);
        if (userFilter == null) {
            throw new NoSuchElementException("Фильтр не найден для пользовательского ID: " + userId);
        }
        userFilter.setActive(userFilterDto.isActive());

        if (userFilterDto.getTypeId() == 0) {
            userFilter.setType(null);
        } else {
            userFilter.setType(typeRepository.findById((long) userFilterDto.getTypeId())
                    .orElseThrow(() -> new NoSuchElementException("Тип не найден для ID: " + userFilterDto.getTypeId())));
        }

        if (userFilterDto.getDealTypeId() == 0) {
            userFilter.setDealType(null);
        } else {
            userFilter.setDealType(dealTypeRepository.findById((long) userFilterDto.getDealTypeId())
                    .orElseThrow(() -> new NoSuchElementException("Тип сделки не найден для ID: " + userFilterDto.getDealTypeId())));
        }
        userFilter.setRoomsCount(userFilterDto.getRoomsCount());
        userFilter.setMaxPrice(userFilterDto.getMaxPrice());
        userFilter.setCity(userFilterDto.getCity());
        userFilterRepository.save(userFilter);
    }
}
