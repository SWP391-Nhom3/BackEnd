package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {
}
