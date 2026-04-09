package com.ptit.backend.service;

import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}

