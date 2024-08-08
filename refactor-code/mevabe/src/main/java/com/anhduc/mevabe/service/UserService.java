package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.UserCreationRequest;
import com.anhduc.mevabe.dto.request.UserUpdateRequest;
import com.anhduc.mevabe.dto.response.UserResponse;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.enums.Role;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse create(UserCreationRequest request) {
        User user = new User();
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    public void delete(UUID userId) {
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private UserResponse convertToResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }
}
