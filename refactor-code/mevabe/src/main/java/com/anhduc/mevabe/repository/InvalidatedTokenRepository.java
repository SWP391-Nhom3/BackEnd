package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, UUID> {
}
