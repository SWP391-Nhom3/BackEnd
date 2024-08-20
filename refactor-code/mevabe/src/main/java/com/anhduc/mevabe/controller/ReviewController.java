package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateBatchRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.BatchResponse;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.entity.ReviewReply;
import com.anhduc.mevabe.service.BatchService;
import com.anhduc.mevabe.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    ApiResponse<Void> add(@RequestBody @Valid Review request) throws IOException {
        reviewService.create(request);
        return ApiResponse.<Void>builder()
                .message("Review created successfully")
                .build();
    }


    @GetMapping
    ApiResponse<List<Review>> getAll() {
        return ApiResponse.<List<Review>>builder()
                .data(reviewService.findAll()).build();
    }

    @GetMapping("/{productId}/{userId}")
    public ApiResponse<List<Review>> getReview(@PathVariable UUID productId, @PathVariable UUID userId) {
        List<Review> reviews = reviewService.getReviews(productId, userId);
        return ApiResponse.<List<Review>>builder()
                .data(reviews)
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<Review> getById(@PathVariable UUID id) {
        return ApiResponse.<Review>builder()
                .data(reviewService.findById(id)).build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        reviewService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Review deleted successfully")
                .build();
    }
}
