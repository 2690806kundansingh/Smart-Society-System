package com.smartsociety.user.security.oauth2;

import com.smartsociety.user.entity.Role;
import com.smartsociety.user.entity.User;
import com.smartsociety.user.repository.RoleRepository;
import com.smartsociety.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("Processing OAuth2 login callback for provider: [{}]", registrationId);

        String email = null;
        String name = null;
        String nameKey = "sub";

        if ("google".equalsIgnoreCase(registrationId)) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            nameKey = "sub";
        } else if ("github".equalsIgnoreCase(registrationId)) {
            name = oAuth2User.getAttribute("name");
            String login = oAuth2User.getAttribute("login");
            email = oAuth2User.getAttribute("email");
            if (email == null && login != null) {
                email = login + "@users.noreply.github.com";
            }
            if (name == null) {
                name = login;
            }
            nameKey = "id";
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider " + registrationId);
        }

        User user = processUserAccount(email, name);
        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes(), nameKey);
    }

    private User processUserAccount(String email, String fullName) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("OAuth2 login matched existing user account ID: {}", user.getId());
            return user;
        }

        log.info("Registering new resident account via OAuth2 for email: {}", email);

        Role residentRole = roleRepository.findByName("ROLE_RESIDENT")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_RESIDENT")));

        User newUser = User.builder()
                .email(email)
                .fullName(fullName != null ? fullName : "Society Member")
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .societyId(1L)
                .roles(new HashSet<>(Collections.singletonList(residentRole)))
                .isActive(true)
                .isAvailable(true)
                .build();

        return userRepository.save(newUser);
    }
}
