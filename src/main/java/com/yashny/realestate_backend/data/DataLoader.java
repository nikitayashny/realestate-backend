package com.yashny.realestate_backend.data;

import com.yashny.realestate_backend.entities.User;
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

    @Override
    public void run(String... args) {
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
