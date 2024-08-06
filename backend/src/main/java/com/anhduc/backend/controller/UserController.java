package com.anhduc.backend.controller;

import com.anhduc.backend.dto.request.UserRequestDto;
import com.anhduc.backend.dto.response.ApiResponse;
import com.anhduc.backend.dto.response.UserResponseDto;
import com.anhduc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        return ApiResponse.<List<UserResponseDto>>builder()
                .status(HttpStatus.OK)
                .data(userService.findAll())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponseDto> getUserById(@PathVariable UUID userId) {
        return ApiResponse.<UserResponseDto>builder()
                .status(HttpStatus.OK)
                .data(userService.findById(userId))
                .build();
    }

    @PostMapping
    public ApiResponse<Void> createUser(@RequestBody UserRequestDto userRequestDto) {
        userService.save(userRequestDto);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED)
                .message("Created user successfully")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteUser(@RequestBody UserRequestDto userRequestDto) {
        userService.deleteById(userRequestDto.getId());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK)
                .message("Deleted user successfully")
                .build();
    }




}
