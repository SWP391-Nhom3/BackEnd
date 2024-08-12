package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateBrandRequest;
import com.anhduc.mevabe.dto.request.CreateCategoryRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.BrandResponse;
import com.anhduc.mevabe.dto.response.CategoryResponse;
import com.anhduc.mevabe.service.BrandService;
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
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {

    BrandService brandService;

    @GetMapping
    ApiResponse<List<BrandResponse>> getAll() {
        return ApiResponse.<List<BrandResponse>>builder()
                .data(brandService.findAll())
                .build();
    }

    @PostMapping
    ApiResponse<BrandResponse> add(@RequestBody CreateBrandRequest request) {
        brandService.create(request);
        return ApiResponse.<BrandResponse>builder()
                .message("Brand added successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteById(@PathVariable UUID id) {
        brandService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Brand deleted successfully")
                .build();
    }
}
