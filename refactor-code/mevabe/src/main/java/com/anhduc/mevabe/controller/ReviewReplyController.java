package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.entity.ReviewReply;
import com.anhduc.mevabe.service.ReviewReplyService;
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
@RequestMapping("/api/review-reply")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewReplyController {

    ReviewReplyService reviewService;

    @PostMapping
    ApiResponse<Void> add(@RequestBody @Valid ReviewReply request) throws IOException {
        reviewService.create(request);
        return ApiResponse.<Void>builder()
                .message("Review reply created successfully")
                .build();
    }


    @GetMapping
    ApiResponse<List<ReviewReply>> getAll() {
        return ApiResponse.<List<ReviewReply>>builder()
                .data(reviewService.findAll()).build();
    }

    @GetMapping("/{id}")
    ApiResponse<ReviewReply> getById(@PathVariable UUID id) {
        return ApiResponse.<ReviewReply>builder()
                .data(reviewService.findById(id)).build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        reviewService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Review reply deleted successfully")
                .build();
    }

}
