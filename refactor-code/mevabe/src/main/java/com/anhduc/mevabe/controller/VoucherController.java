package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.CreateProductRequest;
import com.anhduc.mevabe.dto.request.CreateVoucherRequest;
import com.anhduc.mevabe.dto.response.ApiResponse;
import com.anhduc.mevabe.dto.response.ProductResponse;
import com.anhduc.mevabe.dto.response.VoucherResponse;
import com.anhduc.mevabe.service.ProductService;
import com.anhduc.mevabe.service.VoucherService;
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
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {

    VoucherService voucherService;

    @PostMapping
    public ApiResponse<VoucherResponse> createVoucher(@Valid @RequestBody CreateVoucherRequest request) {
        return ApiResponse.<VoucherResponse>builder()
                .data(voucherService.create(request)).build();
    }

}
