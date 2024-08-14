package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateCategoryRequest;
import com.anhduc.mevabe.dto.request.VoucherRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.CategoryResponse;
import com.anhduc.mevabe.dto.response.VoucherResponse;
import com.anhduc.mevabe.service.CategoryService;
import com.anhduc.mevabe.service.VoucherService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {

    VoucherService voucherService;

    @GetMapping
    ApiResponse<List<VoucherResponse>> getAll() {
        return ApiResponse.<List<VoucherResponse>>builder()
                .data(voucherService.findAll())
                .build();
    }

    @PostMapping
    ApiResponse<VoucherResponse> add(@RequestBody VoucherRequest request) {
        voucherService.create(request);
        return ApiResponse.<VoucherResponse>builder()
                .message("Voucher added successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteById(@PathVariable UUID id) {
        voucherService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Voucher deleted successfully")
                .build();
    }
}
