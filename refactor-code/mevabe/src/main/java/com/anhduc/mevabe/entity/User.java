package com.anhduc.mevabe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User extends AuditAble{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String email;
    String password;
    String firstName;
    String lastName;
//    private String phone;
//    private int pointsBalance = 0;
//    private String emailVerificationToken;
    @ManyToMany
    Set<Role> roles;
}
