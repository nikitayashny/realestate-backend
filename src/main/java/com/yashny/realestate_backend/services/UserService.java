package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.UserFilterRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFilterRepository userFilterRepository;

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);

        UserFilter userFilter = new UserFilter();
        userFilter.setUser(user);
        userFilterRepository.save(userFilter);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
