package com.anhduc.backend.service;

import com.anhduc.backend.entity.Role;
import com.anhduc.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface RoleService {
    void save(String name);
    void delete(String name);
    Optional<Role> findByName(String name);
}

@Service
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public void save(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
    }

    @Override
    public void delete(String name) {
        roleRepository.deleteByName(name);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

}
