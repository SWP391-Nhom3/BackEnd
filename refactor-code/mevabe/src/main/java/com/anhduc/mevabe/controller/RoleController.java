package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.RoleRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.RoleResponse;
import com.anhduc.mevabe.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .data(roleService.findAll())
                .build();
    }

    @PostMapping
    ApiResponse<RoleResponse> add(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .data(roleService.create(request))
                .build();
    }

    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteById(@PathVariable UUID roleId) {
        roleService.deleteById(roleId);
        return ApiResponse.<Void>builder()
                .message("Role deleted")
                .build();
    }
}
