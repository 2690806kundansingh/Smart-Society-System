package com.smartsociety.user.service;

import com.smartsociety.user.dto.*;
import com.smartsociety.user.entity.*;
import com.smartsociety.user.exception.InvalidCredentialsException;
import com.smartsociety.user.exception.ResourceNotFoundException;
import com.smartsociety.user.exception.UserAlreadyExistsException;
import com.smartsociety.user.repository.ApartmentRepository;
import com.smartsociety.user.repository.RoleRepository;
import com.smartsociety.user.repository.UserRepository;
import com.smartsociety.user.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApartmentRepository apartmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       ApartmentRepository apartmentRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.apartmentRepository = apartmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String accessToken = tokenProvider.generateAccessToken(authentication, user.getId(), user.getSocietyId());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenValidityMs() / 1000)
                .user(mapToProfileResponse(user))
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        String targetRoleName = (request.getRole() != null && !request.getRole().isBlank()) 
                ? request.getRole() 
                : "ROLE_RESIDENT";

        Role role = roleRepository.findByName(targetRoleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(targetRoleName).build()));

        Set<Role> roles = new HashSet<>(Collections.singletonList(role));

        Apartment apartment = null;
        if (request.getBlockName() != null && request.getFlatNumber() != null) {
            apartment = apartmentRepository.findBySocietyIdAndBlockNameAndFlatNumber(
                    request.getSocietyId(), request.getBlockName(), request.getFlatNumber()
            ).orElseGet(() -> apartmentRepository.save(
                    Apartment.builder()
                            .societyId(request.getSocietyId())
                            .blockName(request.getBlockName())
                            .flatNumber(request.getFlatNumber())
                            .build()
            ));
        }

        Department department = null;
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            try {
                department = Department.valueOf(request.getDepartment().toUpperCase());
            } catch (IllegalArgumentException e) {
                department = Department.GENERAL;
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .societyId(request.getSocietyId())
                .apartment(apartment)
                .department(department)
                .roles(roles)
                .isActive(true)
                .isAvailable(true)
                .build();

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = tokenProvider.generateAccessToken(authentication, savedUser.getId(), savedUser.getSocietyId());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenValidityMs() / 1000)
                .user(mapToProfileResponse(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new com.smartsociety.user.security.UserPrincipal(user),
                null,
                new com.smartsociety.user.security.UserPrincipal(user).getAuthorities()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication, user.getId(), user.getSocietyId());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenValidityMs() / 1000)
                .user(mapToProfileResponse(user))
                .build();
    }

    public UserProfileResponse mapToProfileResponse(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String apartmentInfo = null;
        if (user.getApartment() != null) {
            apartmentInfo = "Block " + user.getApartment().getBlockName() + " - Flat " + user.getApartment().getFlatNumber();
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .societyId(user.getSocietyId())
                .roles(roleNames)
                .department(user.getDepartment())
                .apartment(apartmentInfo)
                .isAvailable(user.getIsAvailable())
                .isActive(user.getIsActive())
                .build();
    }
}
