package com.anhduc.mevabe.service;

import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.entity.ReviewReply;
import com.anhduc.mevabe.repository.ReviewReplyRepository;
import com.anhduc.mevabe.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewReplyService {

    ReviewReplyRepository reviewReplyRepository;
    ReviewRepository reviewRepository;

    public void create(ReviewReply reviewReply) {
        reviewReplyRepository.save(reviewReply);
    }

    public List<ReviewReply> findAll() {
        return reviewReplyRepository.findAll();
    }
    public List<ReviewReply> getRepliesByProduct(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        List<ReviewReply> replies = new ArrayList<>();
        for (Review review : reviews) {
            replies.addAll(reviewReplyRepository.findAllByReviewId(review.getId()));
        }
        return replies;
    }

    public List<ReviewReply> getRepliesByUser(UUID userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<ReviewReply> replies = new ArrayList<>();
        for (Review review : reviews) {
            replies.addAll(reviewReplyRepository.findAllByReviewId(review.getId()));
        }
        return replies;
    }

    public void delete(UUID id) {
        reviewReplyRepository.deleteById(id);
    }

    public ReviewReply findById(UUID id) {
        return reviewReplyRepository.findById(id).orElse(null);
    }

}
