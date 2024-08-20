package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreatePasswordRequest;
import com.anhduc.mevabe.dto.request.UserCreationRequest;
import com.anhduc.mevabe.dto.request.UserUpdateRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.UserResponse;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.repository.UserRepository;
import com.anhduc.mevabe.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

//    @PostMapping
//    ApiResponse<UserResponse> addUser(@RequestBody @Valid UserCreationRequest request) {
//        return ApiResponse.<UserResponse>builder()
//                .data(userService.create(request))
//                .build();
//    }

    @PostMapping("/create-staff")
    public ApiResponse<UserResponse> createStaff(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createStaff(request))
                .build();
    }

    @PostMapping("/create-shipper")
    public ApiResponse<UserResponse> createShipper(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.createShipper(request))
                .build();
    }

    @PostMapping("/create-password")
    ApiResponse<Void> createPassword(@RequestBody @Valid CreatePasswordRequest request) {
        userService.createPassword(request);
        return ApiResponse.<Void>builder()
                .message("Successfully created password, you could use it to login")
                .build();
    }


    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .data(userService.getAll()).build();
    }

    @GetMapping("/shippers")
    ApiResponse<List<UserResponse>> getAllShippers() {
        return  ApiResponse.<List<UserResponse>>builder().data(userService.getShipper()).build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserByID(@PathVariable UUID userId) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUserById(userId)).build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getMyInfo()).build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<Void> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        return ApiResponse.<Void>builder()
                .message("User has been deleted")
                .build();
    }

    @PutMapping
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateMyInfo(request)).build();
    }

    @PutMapping("/status/{userId}")
    public ApiResponse<UserResponse> updateStatus(@PathVariable UUID userId) {
        UserResponse userResponse = userService.updateStatus(userId);
        return ApiResponse.<UserResponse>builder()
                .data(userResponse)
                .build();
    }
}
