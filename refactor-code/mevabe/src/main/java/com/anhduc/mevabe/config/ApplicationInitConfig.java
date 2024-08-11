package com.anhduc.mevabe.config;

import com.anhduc.mevabe.entity.Permission;
import com.anhduc.mevabe.entity.Role;
import com.anhduc.mevabe.entity.User;
//import com.anhduc.mevabe.enums.Role;
import com.anhduc.mevabe.repository.PermissionRepository;
import com.anhduc.mevabe.repository.RoleRepository;
import com.anhduc.mevabe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;


    @Bean
    ApplicationRunner applicationRunner() {
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
            // Check if roles already exist to avoid duplication
            if (roleRepository.count() == 0 && permissionRepository.count() == 0) {
                log.info("Initializing roles, permissions, and default users.");

                // Guest permissions
                Permission viewProducts = new Permission();
                viewProducts.setName("VIEW_PRODUCTS");
                viewProducts.setDescription("Xem thông tin, xem đánh giá, tìm kiếm các sản phẩm về sữa và mua hàng.");

                Permission viewHealthArticles = new Permission();
                viewHealthArticles.setName("VIEW_HEALTH_ARTICLES");
                viewHealthArticles.setDescription("Xem các bài viết về chăm sóc sức khỏe cho mẹ bầu và em bé.");

                Permission purchaseFromArticles = new Permission();
                purchaseFromArticles.setName("PURCHASE_FROM_ARTICLES");
                purchaseFromArticles.setDescription("Mua hàng trực tiếp từ các sản phẩm được gợi ý từ các bài viết.");

                // Member permissions
                Permission onlinePayment = new Permission();
                onlinePayment.setName("ONLINE_PAYMENT");
                onlinePayment.setDescription("Mua hàng bằng các hình thức chuyển khoản, thanh toán trực tuyến.");

                Permission useVoucher = new Permission();
                useVoucher.setName("USE_VOUCHER");
                useVoucher.setDescription("Sử dụng voucher, tích lũy điểm thành viên, đổi quà hoặc sản phẩm.");

                Permission reviewProducts = new Permission();
                reviewProducts.setName("REVIEW_PRODUCTS");
                reviewProducts.setDescription("Đánh giá, feedback sản phẩm.");

                Permission chatConsultation = new Permission();
                chatConsultation.setName("CHAT_CONSULTATION");
                chatConsultation.setDescription("Đăng ký tư vấn chọn và mua sữa online.");

                Permission preOrder = new Permission();
                preOrder.setName("PRE_ORDER");
                preOrder.setDescription("Đặt hàng trước khi chưa có sẵn sản phẩm.");

                // Staff permissions
                Permission confirmOrders = new Permission();
                confirmOrders.setName("CONFIRM_ORDERS");
                confirmOrders.setDescription("Xác nhận các đơn hàng.");

                Permission manageInventory = new Permission();
                manageInventory.setName("MANAGE_INVENTORY");
                manageInventory.setDescription("Quản lý hàng hóa.");

                Permission manageUsers = new Permission();
                manageUsers.setName("MANAGE_USERS");
                manageUsers.setDescription("Quản lý người dùng.");

                Permission createVouchers = new Permission();
                createVouchers.setName("CREATE_VOUCHERS");
                createVouchers.setDescription("Tạo các mã voucher.");

                Permission processReports = new Permission();
                processReports.setName("PROCESS_REPORTS");
                processReports.setDescription("Xử lý report, tracking đơn hàng.");

                Permission manageArticles = new Permission();
                manageArticles.setName("MANAGE_ARTICLES");
                manageArticles.setDescription("Quản lý các bài viết.");

                // Admin permissions
                Permission manageAccounts = new Permission();
                manageAccounts.setName("MANAGE_ACCOUNTS");
                manageAccounts.setDescription("Quản lý account.");

                Permission viewStatistics = new Permission();
                viewStatistics.setName("VIEW_STATISTICS");
                viewStatistics.setDescription("Thống kê doanh thu, sản phẩm.");

                // Save all permissions to the database
                permissionRepository.saveAll(List.of(
                        viewProducts, viewHealthArticles, purchaseFromArticles,
                        onlinePayment, useVoucher, reviewProducts, chatConsultation, preOrder,
                        confirmOrders, manageInventory, manageUsers, createVouchers, processReports, manageArticles,
                        manageAccounts, viewStatistics
                ));

                // Create roles and assign permissions
                Role guestRole = new Role();
                guestRole.setName("GUEST");
                guestRole.setDescription("Guest role");
                guestRole.setPermissions(Set.of(viewProducts, viewHealthArticles, purchaseFromArticles));

                Role memberRole = new Role();
                memberRole.setName("MEMBER");
                memberRole.setDescription("Member role");
                memberRole.setPermissions(Set.of(onlinePayment, useVoucher, reviewProducts, chatConsultation, preOrder));

                Role staffRole = new Role();
                staffRole.setName("STAFF");
                staffRole.setDescription("Staff role");
                staffRole.setPermissions(Set.of(confirmOrders, manageInventory, manageUsers, createVouchers, processReports, manageArticles));

                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("Admin role");
                adminRole.setPermissions(Set.of(manageAccounts, viewStatistics));

                // Save roles to the database
                roleRepository.saveAll(List.of(guestRole, memberRole, staffRole, adminRole));

                // Create users with corresponding roles
                User guestUser = User.builder()
                        .email("guest@example.com")
                        .password(passwordEncoder.encode("guestpassword"))
                        .firstName("Guest")
                        .lastName("User")
                        .roles(Set.of(guestRole))
                        .build();

                User memberUser = User.builder()
                        .email("member@example.com")
                        .password(passwordEncoder.encode("memberpassword"))
                        .firstName("Member")
                        .lastName("User")
                        .roles(Set.of(memberRole))
                        .build();

                User staffUser = User.builder()
                        .email("staff@example.com")
                        .password(passwordEncoder.encode("staffpassword"))
                        .firstName("Staff")
                        .lastName("User")
                        .roles(Set.of(staffRole))
                        .build();

                User adminUser = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("adminpassword"))
                        .firstName("Admin")
                        .lastName("User")
                        .roles(Set.of(adminRole))
                        .build();

                // Save users to the database
                userRepository.saveAll(List.of(guestUser, memberUser, staffUser, adminUser));

                log.info("Default users have been created successfully.");
            } else {
                log.info("Roles, permissions, and default users already exist. Skipping initialization.");
            }
        };
    }
