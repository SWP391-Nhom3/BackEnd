package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.PermissionRequest;
import com.anhduc.mevabe.dto.response.PermissionResponse;
import com.anhduc.mevabe.entity.Permission;
import com.anhduc.mevabe.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    ModelMapper modelMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = modelMapper.map(request, Permission.class);
        return modelMapper.map(permissionRepository.save(permission), PermissionResponse.class);
    }

    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PermissionResponse convertToResponse(Permission permission) {
        return modelMapper.map(permission, PermissionResponse.class);
    }

    public void deleteById(UUID id) {
        permissionRepository.deleteById(id);
    }
}
