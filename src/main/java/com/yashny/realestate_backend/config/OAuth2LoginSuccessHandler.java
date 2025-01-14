package com.yashny.realestate_backend.config;

import com.yashny.realestate_backend.entities.User;
import com.yashny.realestate_backend.repositories.UserRepository;
import com.yashny.realestate_backend.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        if (oAuth2User == null) {
            throw new OAuth2AuthenticationException("Unable to load user");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profilePicture = oAuth2User.getAttribute("picture");

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setProfilePicture(profilePicture);
            newUser.setProvider("GOOGLE");
            newUser.setEnabled(true);
            newUser.setRole("USER");
            userRepository.save(newUser);
        }

        String jwt = jwtUtil.generateToken(name, email, existingUser.isPresent() ? existingUser.get().getRole() : "USER");

        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + jwt + "\"}");

        response.addHeader("Authorization", "Bearer " + jwt);
    }
}