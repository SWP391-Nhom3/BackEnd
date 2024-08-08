package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.UserCreationRequest;
import com.anhduc.mevabe.dto.request.UserUpdateRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.UserResponse;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> addUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAll()).build();
    }

    @GetMapping("/{userId}")
    User getUserByID(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ApiResponse.<Void>builder()
                .message("User has been deleted")
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest request, @PathVariable UUID userId) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.update(userId, request)).build();
    }
}
