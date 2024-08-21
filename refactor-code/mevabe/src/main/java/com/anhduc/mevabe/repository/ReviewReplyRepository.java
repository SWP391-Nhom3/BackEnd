package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {
    List<ReviewReply> findAllByReviewId(UUID reviewId);
}
