package com.ptit.backend.service;

import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.request.RegisterRequest;
import com.ptit.backend.dto.response.LoginResponse;
import com.ptit.backend.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse loginWithGoogle(String firebaseIdToken);

    UserResponse me(Authentication authentication);

    UserResponse register(RegisterRequest request);
}

