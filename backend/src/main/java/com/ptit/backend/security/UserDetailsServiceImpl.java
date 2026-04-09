package com.ptit.backend.security;

import com.ptit.backend.entity.User;
import com.ptit.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay nguoi dung"));

        String normalizedRole = normalizeRole(user);
        boolean enabled = user.getStatus() == null || user.getStatus() == 1;

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(normalizedRole))
                .disabled(!enabled)
                .build();
    }

    private String normalizeRole(User user) {
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        if (!StringUtils.hasText(roleName)) {
            return "ROLE_USER";
        }

        String trimmed = roleName.trim().toUpperCase();
        return trimmed.startsWith("ROLE_") ? trimmed : "ROLE_" + trimmed;
    }
}

