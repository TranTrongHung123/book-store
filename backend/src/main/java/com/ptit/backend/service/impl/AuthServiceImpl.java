package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.request.RegisterRequest;
import com.ptit.backend.dto.request.UserRequest;
import com.ptit.backend.dto.response.LoginResponse;
import com.ptit.backend.dto.response.UserResponse;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.security.JwtTokenProvider;
import com.ptit.backend.service.AuthService;
import com.ptit.backend.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

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
}
