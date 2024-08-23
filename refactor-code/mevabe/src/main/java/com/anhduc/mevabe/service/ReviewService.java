package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateBrandRequest;
import com.anhduc.mevabe.dto.response.BrandResponse;
import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.entity.ReviewReply;
import com.anhduc.mevabe.repository.BrandRepository;
import com.anhduc.mevabe.repository.ReviewReplyRepository;
import com.anhduc.mevabe.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    ReviewRepository reviewRepository;
    ReviewReplyRepository reviewReplyRepository;
    ProductService productService;

    public void create(Review review) {
        reviewRepository.save(review);
        productService.updateProductRating(review.getProduct().getId());
    }

    public Review getReviews(UUID productId, UUID userId) {
        if (productId == null || userId == null) {
            throw new IllegalArgumentException("Product ID and User ID must not be null");
        }
        return reviewRepository.findByProductIdAndUserId(productId, userId);
    }

    public List<Review> getReviewsByUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Product ID and User ID must not be null");
        }
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByProduct(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID and User ID must not be null");
        }
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(UUID id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        reviewRepository.deleteById(id);
    }



}
