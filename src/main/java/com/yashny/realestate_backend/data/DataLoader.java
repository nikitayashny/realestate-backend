package com.yashny.realestate_backend.data;

import com.yashny.realestate_backend.entities.DealType;
import com.yashny.realestate_backend.entities.Type;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.DealTypeRepository;
import com.yashny.realestate_backend.repositories.TypeRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TypeRepository typeRepository;
    private final DealTypeRepository dealTypeRepository;

    @Override
    public void run(String... args) {
        if (typeRepository.count() == 0) {
            typeRepository.save(new Type(null, "Квартира"));
            typeRepository.save(new Type(null, "Дом"));
            typeRepository.save(new Type(null, "Офис"));
            typeRepository.save(new Type(null, "Гараж"));
            typeRepository.save(new Type(null, "Торговое помещение"));
        }
        if (dealTypeRepository.count() == 0) {
            dealTypeRepository.save(new DealType(null, "Аренда"));
            dealTypeRepository.save(new DealType(null, "Продажа"));
            dealTypeRepository.save(new DealType(null, "Посуточно"));
        }
        if (userRepository.count() == 0) {
            User super_admin = new User();
            super_admin.setRole("SUPER_ADMIN");
            super_admin.setUsername("Главный Администратор");
            super_admin.setPassword(passwordEncoder.encode("1"));
            super_admin.setEmail("homehuboff@gmail.com");
            userRepository.save(super_admin);
        }
    }
}
