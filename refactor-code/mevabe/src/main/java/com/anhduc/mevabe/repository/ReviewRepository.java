package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Review findByProductIdAndUserId(UUID productId, UUID userId);
}
