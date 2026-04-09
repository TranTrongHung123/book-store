package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.LoginRequest;
import com.ptit.backend.dto.response.LoginResponse;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.security.JwtTokenProvider;
import com.ptit.backend.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword())
            );

            UserDetails principal = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(principal);
            List<String> roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getExpirationMs() / 1000)
                    .username(principal.getUsername())
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
}


