package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateProductRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.ProductResponse;
import com.anhduc.mevabe.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @PostMapping
    ApiResponse<Void> add(@ModelAttribute @Valid CreateProductRequest request) throws IOException {
        productService.create(request);
        return ApiResponse.<Void>builder()
                .message("Product created successfully")
                .build();
    }


    @GetMapping
    ApiResponse<List<ProductResponse>> getAll() {
        return ApiResponse.<List<ProductResponse>>builder()
                .data(productService.findAll()).build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getById(@PathVariable UUID id) {
        return ApiResponse.<ProductResponse>builder()
                .data(productService.findById(id)).build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully")
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ProductResponse> update(@RequestBody @Valid CreateProductRequest request, @PathVariable UUID id) {
        return ApiResponse.<ProductResponse>builder()
                .data(productService.update(id, request)).build();
    }
}
