package com.anhduc.mevabe.config;

import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.entity.Role;
//import com.anhduc.mevabe.enums.Role;
import com.anhduc.mevabe.repository.RoleRepository;
import com.anhduc.mevabe.repository.UserRepository;
import com.anhduc.mevabe.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            initializeRoles();
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found")));
                User user = new User();
                user.setEmail("admin@gmail.com");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setFirstName("admin");
                user.setLastName("admin");
                user.setRoles(roles);
                userRepository.save(user);
                log.warn("Account admin has been created with email admin@gmail.com and password admin");
            }
        };
    };

    private void initializeRoles() {
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator role");
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByName("MEMBER")) {
            Role userRole = new Role();
            userRole.setName("MEMBER");
            userRole.setDescription("Member role");
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName("STAFF")) {
            Role staffRole = new Role();
            staffRole.setName("STAFF");
            staffRole.setDescription("Staff role");
            roleRepository.save(staffRole);
        }
    }
}
