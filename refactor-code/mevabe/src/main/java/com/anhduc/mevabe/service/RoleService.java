package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.RoleRequest;
import com.anhduc.mevabe.dto.response.RoleResponse;
import com.anhduc.mevabe.entity.Role;
import com.anhduc.mevabe.repository.PermissionRepository;
import com.anhduc.mevabe.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

    RoleRepository roleRepository;
    ModelMapper modelMapper;
    PermissionRepository permissionRepository;

//    public RoleResponse create(RoleRequest request) {
//        Role role = modelMapper.map(request, Role.class);
//        var permissions = permissionRepository.findAllById(request.getPermissions());
//        role.setPermissions(new HashSet<>(permissions));
//        return modelMapper.map(roleRepository.save(role), RoleResponse.class);
//    }

    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream()
                .map(this::convertRoleToRoleResponse)
                .toList();
    }

//    public void deleteById(UUID id) {
//        roleRepository.deleteById(id);
//    }

    private RoleResponse convertRoleToRoleResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }
}
