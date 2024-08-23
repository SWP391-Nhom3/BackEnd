package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreatePasswordRequest;
import com.anhduc.mevabe.dto.request.PasswordUpdateRequest;
import com.anhduc.mevabe.dto.request.UserCreationRequest;
import com.anhduc.mevabe.dto.request.UserUpdateRequest;
import com.anhduc.mevabe.dto.response.UserResponse;
import com.anhduc.mevabe.entity.Role;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.RoleRepository;
import com.anhduc.mevabe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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
    RoleRepository roleRepository;


    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createStaff(UserCreationRequest request) {
        return createUser(request, "STAFF");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createShipper(UserCreationRequest request) {
        return createUser(request, "SHIPPER");
    }

    private UserResponse createUser(UserCreationRequest request, String roleName) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = new User();
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(roleName).get();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setActive(true);
        user.setPoint(BigDecimal.ZERO);
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }



    public UserResponse updateMyInfo(UserUpdateRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        modelMapper.map(request, user);
        userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }

    public void updateMyPassword(PasswordUpdateRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    public void delete(UUID userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<UserResponse> getShipper(){
        Role shipper = roleRepository.findByName("SHIPPER").get();
        List<User> shippers = userRepository.findByRoles(shipper);
        return shippers.stream().map(this::convertToResponse).toList();
    }

    private UserResponse convertToResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        return modelMapper.map(user, UserResponse.class);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        var userResponse = modelMapper.map(user, UserResponse.class);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));
        return userResponse;
    }

    public void createPassword(CreatePasswordRequest request) {

        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
