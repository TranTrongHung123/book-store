package com.ptit.backend.service.impl;

import com.ptit.backend.dto.request.UserRequest;
import com.ptit.backend.dto.response.UserResponse;
import com.ptit.backend.entity.Role;
import com.ptit.backend.entity.User;
import com.ptit.backend.exception.AppException;
import com.ptit.backend.exception.ErrorCode;
import com.ptit.backend.mapper.UserMapper;
import com.ptit.backend.repository.RoleRepository;
import com.ptit.backend.repository.UserRepository;
import com.ptit.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Ten dang nhap la bat buoc");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Mat khau la bat buoc");
        }
        if (request.getRoleId() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Truong role_id la bat buoc");
        }
        validateUserRequest(request, null);
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = userMapper.toEntity(request);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (user.getTotalPoints() == null) {
            user.setTotalPoints(0);
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        validateUserRequest(request, id);

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            existingUser.setRole(role);
        }

        if (StringUtils.hasText(request.getUsername())) {
            existingUser.setUsername(request.getUsername().trim());
        }
        if (StringUtils.hasText(request.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail().trim());
        }
        if (request.getPhone() != null) {
            existingUser.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            existingUser.setAddress(request.getAddress());
        }
        if (request.getFullName() != null) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getTotalPoints() != null) {
            if (request.getTotalPoints() < 0) {
                throw new AppException(ErrorCode.INVALID_TOTAL_POINTS);
            }
            existingUser.setTotalPoints(request.getTotalPoints());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    private void validateUserRequest(UserRequest request, Long currentUserId) {
        if (request.getRoleId() != null && !roleRepository.existsById(request.getRoleId())) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        if (StringUtils.hasText(request.getUsername())) {
            userRepository.findByUsername(request.getUsername().trim())
                    .filter(user -> !user.getUserId().equals(currentUserId))
                    .ifPresent(user -> {
                        throw new AppException(ErrorCode.DUPLICATE_USERNAME);
                    });
        }

        if (StringUtils.hasText(request.getEmail())) {
            userRepository.findByEmail(request.getEmail().trim())
                    .filter(user -> !user.getUserId().equals(currentUserId))
                    .ifPresent(user -> {
                        throw new AppException(ErrorCode.DUPLICATE_EMAIL);
                    });
        }

        if (request.getTotalPoints() != null && request.getTotalPoints() < 0) {
            throw new AppException(ErrorCode.INVALID_TOTAL_POINTS);
        }
    }
}

