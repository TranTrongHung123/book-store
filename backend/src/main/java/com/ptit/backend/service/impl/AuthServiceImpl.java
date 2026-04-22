package com.ptit.backend.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.request.RegisterRequest;
import com.ptit.backend.dto.request.UserRequest;
import com.ptit.backend.dto.response.LoginResponse;
import com.ptit.backend.dto.response.UserResponse;
import com.ptit.backend.entity.Role;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.UserMapper;
import com.ptit.backend.repository.RoleRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.security.JwtTokenProvider;
import com.ptit.backend.service.AuthService;
import com.ptit.backend.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            String identifier = request.getUsername().trim();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
            );

            String resolvedUsername = authentication.getName();
            User user = userRepository.findByUsername(resolvedUsername)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "USER";
            primaryRole = primaryRole == null ? "USER" : primaryRole.trim().toUpperCase();
            if (primaryRole.startsWith("ROLE_")) {
                primaryRole = primaryRole.substring(5);
            }

            String token = jwtTokenProvider.generateToken(user);
            List<String> roles = List.of("ROLE_" + primaryRole);

            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                    .username(user.getUsername())
                    .roles(roles)
                    .build();
        } catch (BadCredentialsException exception) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (DisabledException exception) {
            throw new AppException(ErrorCode.FORBIDDEN, "Tai khoan da bi khoa");
        } catch (AuthenticationException exception) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    @Transactional
    public LoginResponse loginWithGoogle(String firebaseIdToken) {
        FirebaseToken firebaseToken = verifyFirebaseIdToken(firebaseIdToken);
        String email = normalizeEmail(firebaseToken.getEmail());
        String fullName = normalizeFullName(firebaseToken.getName(), email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUserFromGoogle(email, fullName));

        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new AppException(ErrorCode.FORBIDDEN, "Tai khoan da bi khoa");
        }

        String primaryRole = user.getRole() != null ? user.getRole().getRoleName() : "USER";
        primaryRole = primaryRole == null ? "USER" : primaryRole.trim().toUpperCase();
        if (primaryRole.startsWith("ROLE_")) {
            primaryRole = primaryRole.substring(5);
        }

        String token = jwtTokenProvider.generateToken(user);
        List<String> roles = List.of("ROLE_" + primaryRole);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(Authentication authentication) {
        User user = resolveAuthenticatedUser(authentication);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        UserRequest createRequest = UserRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .status(1)
                .totalPoints(0)
                .build();

        return userService.createUser(createRequest);
    }

    private FirebaseToken verifyFirebaseIdToken(String firebaseIdToken) {
        if (!StringUtils.hasText(firebaseIdToken)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Firebase ID token khong duoc de trong");
        }
        try {
            return FirebaseAuth.getInstance().verifyIdToken(firebaseIdToken.trim());
        } catch (FirebaseAuthException exception) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Firebase ID token khong hop le");
        }
    }

    private String normalizeEmail(String email) {
        String normalizedEmail = String.valueOf(email).trim().toLowerCase();
        if (!StringUtils.hasText(normalizedEmail)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Khong lay duoc email tu tai khoan Google");
        }
        return normalizedEmail;
    }

    private String normalizeFullName(String firebaseName, String email) {
        if (StringUtils.hasText(firebaseName)) {
            return firebaseName.trim();
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return "Google User";
        }
        return email.substring(0, atIndex);
    }

    private User createUserFromGoogle(String email, String fullName) {
        String username = generateUniqueUsername(email);
        Role role = resolveDefaultUserRole();

        User user = User.builder()
                .role(role)
                .username(username)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .email(email)
                .fullName(fullName)
                .status(1)
                .totalPoints(0)
                .build();
        return userRepository.save(user);
    }

    private Role resolveDefaultUserRole() {
        return roleRepository.findByRoleName("USER")
                .or(() -> roleRepository.findByRoleName("ROLE_USER"))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Khong tim thay vai tro USER"));
    }

    private String generateUniqueUsername(String email) {
        String localPart = email.split("@")[0];
        String base = localPart.replaceAll("[^a-zA-Z0-9._-]", "");
        if (!StringUtils.hasText(base)) {
            base = "googleuser";
        }
        base = base.toLowerCase();
        if (base.length() > 32) {
            base = base.substring(0, 32);
        }

        String candidate = base;
        int suffix = 1;
        while (userRepository.findByUsername(candidate).isPresent()) {
            candidate = base + suffix;
            if (candidate.length() > 50) {
                int trimLength = Math.max(1, 50 - String.valueOf(suffix).length());
                candidate = base.substring(0, Math.min(base.length(), trimLength)) + suffix;
            }
            suffix++;
        }
        return candidate;
    }

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String principalString) {
            username = principalString;
        }

        if (username == null || username.isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String resolvedUsername = username.trim();
        return userRepository.findByUsername(resolvedUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
