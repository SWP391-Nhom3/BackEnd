package com.anhduc.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends AuditAble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    @Column(nullable = false)
    private String password;
    @NaturalId
    private String email;
    @NaturalId
    private String phoneNumber;
    private String avatar;
    private int pointsBalance;
    private Boolean enabled = false;
    private String verificationCode;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();
}
