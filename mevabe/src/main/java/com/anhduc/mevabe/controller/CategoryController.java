package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateCategoryRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.CategoryResponse;
import com.anhduc.mevabe.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @GetMapping
    ApiResponse<List<CategoryResponse>> getAll() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .data(categoryService.findAll())
                .build();
    }

    @PostMapping
    ApiResponse<CategoryResponse> add(@RequestBody CreateCategoryRequest request) {
        categoryService.create(request);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category added successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteById(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Category deleted successfully")
                .build();
    }
}
