package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.PermissionRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.PermissionResponse;
import com.anhduc.mevabe.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @GetMapping
    ApiResponse<List<PermissionResponse>> getPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .data(permissionService.findAll())
                .build();
    }

    @PostMapping
    ApiResponse<PermissionResponse> addPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .data(permissionService.create(request))
                .build();
    }

    @DeleteMapping("/{permissionId}")
    ApiResponse<Void> deletePermission(@PathVariable UUID permissionId) {
        permissionService.deleteById(permissionId);
        return ApiResponse.<Void>builder()
                .message("Permission deleted")
                .build();
    }
}
