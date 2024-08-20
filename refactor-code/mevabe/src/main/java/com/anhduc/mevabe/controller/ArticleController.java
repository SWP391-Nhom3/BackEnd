package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.entity.Article;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.service.ArticleService;
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
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArticleController {

    ArticleService articleService;

    @PostMapping
    ApiResponse<Void> add(@RequestBody @Valid Article request) throws IOException {
        articleService.create(request);
        return ApiResponse.<Void>builder()
                .message("Article created successfully")
                .build();
    }


    @GetMapping
    ApiResponse<List<Article>> getAll() {
        return ApiResponse.<List<Article>>builder()
                .data(articleService.findAll()).build();
    }

    @GetMapping("/{id}")
    ApiResponse<Article> getById(@PathVariable UUID id) {
        return ApiResponse.<Article>builder()
                .data(articleService.findById(id)).build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        articleService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Article deleted successfully")
                .build();
    }

}
