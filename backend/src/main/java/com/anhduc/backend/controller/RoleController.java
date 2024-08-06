package com.anhduc.backend.controller;

import com.anhduc.backend.dto.request.RoleRequestDto;
import com.anhduc.backend.dto.response.ApiResponse;
import com.anhduc.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ApiResponse<Void> createRole(RoleRequestDto dto) {
        roleService.save(dto.getName());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED)
                .message("Role created")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteRole(RoleRequestDto dto) {
        roleService.delete(dto.getName());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK)
                .message("Role deleted")
                .build();
    }
}
