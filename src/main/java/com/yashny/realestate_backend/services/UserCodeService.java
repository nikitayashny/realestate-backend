package com.yashny.realestate_backend.services;

import com.yashny.realestate_backend.entities.UserCode;
import com.yashny.realestate_backend.repositories.UserCodeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCodeService {

    private final UserCodeRepository userCodeRepository;

    public void create(@NonNull String email, String confirmationCode) {
        if (userCodeRepository.findByEmail(email).isPresent()) {
            UserCode userCode = userCodeRepository.findByEmail(email).orElseThrow();
            userCode.setCode(confirmationCode);
            userCodeRepository.save(userCode);
        }
        else {
            UserCode userCode = new UserCode();
            userCode.setEmail(email);
            userCode.setCode(confirmationCode);
            userCodeRepository.save(userCode);
        }

    }

    public UserCode findByEmail(@NonNull String email) {
        return userCodeRepository.findByEmail(email).orElseThrow();
    }
}
