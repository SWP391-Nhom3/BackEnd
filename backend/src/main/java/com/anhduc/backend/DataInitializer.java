package com.anhduc.backend;

import com.anhduc.backend.entity.Role;
import com.anhduc.backend.repository.RoleRepository;
import com.anhduc.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RoleService roleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (roleService.findByName("USER").isEmpty()) {
            roleService.save("USER");
        }
    }
}
