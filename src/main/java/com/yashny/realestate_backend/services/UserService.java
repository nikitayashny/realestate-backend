package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.Realt;
import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.entities.UserFilter;
import com.yashny.realestate_backend.repositories.RealtRepository;
import com.yashny.realestate_backend.repositories.UserFilterRepository;
import com.yashny.realestate_backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFilterRepository userFilterRepository;
    private final RealtRepository realtRepository;

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

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Realt not found with id " + id));
    }

    public List<Realt> getUsersRealts(Long id) {
        List<Realt> realts = realtRepository.findAll();

        realts = realts.stream()
                .filter(realt -> id.equals(realt.getUser().getId()))
                .collect(Collectors.toList());

        return realts;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void banUser(Long id, User userReq) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        if (Objects.equals(userReq.getRole(), "USER")) {
            return;
        }
        if (Objects.equals(user.getRole(), "SUPER_ADMIN")) {
            return;
        }
        if (Objects.equals(user.getRole(), "ADMIN") && Objects.equals(userReq.getRole(), "ADMIN")) {
            return;
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void changeRole(Long id, User userReq) {
        if (!Objects.equals(userReq.getRole(), "SUPER_ADMIN")) {
            return;
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        if (Objects.equals(user.getRole(), "USER")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        userRepository.save(user);
    }
}
