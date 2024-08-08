package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.UserCreationRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    ApiResponse<Void> addUser(@RequestBody @Valid UserCreationRequest request) {
        userService.create(request);
        return ApiResponse.<Void>builder()
                .message("Created user successfully")
                .build();
    }

    @GetMapping("/{userId}")
    User getUserByID(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }
}
